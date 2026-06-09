package gdg.challenge.poom.domain.chat.service;

import gdg.challenge.poom.domain.chat.converter.ChatConverter;
import gdg.challenge.poom.domain.chat.dto.request.ChatRequestDTO;
import gdg.challenge.poom.domain.chat.dto.response.ChatResponseDTO;
import gdg.challenge.poom.domain.chat.entity.ChatRoom;
import gdg.challenge.poom.domain.chat.entity.enums.CharacterType;
import gdg.challenge.poom.domain.chat.entity.enums.MessageType;
import gdg.challenge.poom.domain.chat.entity.enums.SenderType;
import gdg.challenge.poom.domain.chat.service.command.ChatCommandService;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.domain.member.service.GcsService;
import gdg.challenge.poom.global.error.code.status.GeneralErrorCode;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.GeneralException;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ChatHelperService {

    private static final long IMAGE_MAX_BYTES = 10 * 1024 * 1024; // 10MB
    private final GcsService gcsService;

    /** style -> 시스템 프롬프트 파일 경로 (prompts/ 하위) */
    private static final Map<String, String> STYLE_PROMPT_PATHS = Map.of(
            "empathy", "prompts/poom-system-empathy.txt",
            "solution", "prompts/poom-system-solution.txt"
    );

    private static final String DEFAULT_STYLE = CharacterType.EMPATHY.toString(); // 기본 AI 응답 스타일
    private static final String PROVISIONAL_TITLE = "새 대화";
    private static final String IMAGE_ONLY_PROMPT = "첨부한 이미지를 보고 현재 상황을 이해한 뒤 적절한 답변을 해줘."; // 사용자가 이미지만 입력할 경우

    private final ChatClient poomChatClient;
    private final RestTemplate imageFetchRestTemplate;
    private final ChatCommandService chatCommandService;
    private final MemberRepository memberRepository;
    private final Map<String, String> promptCache = new ConcurrentHashMap<>();

    /**
     * 사용자 메시지와 스타일에 따라 AI 응답을 반환합니다.
     * imageUrls가 있으면 해당 URL의 이미지들을 가져와 멀티모달 입력으로 전달합니다.
     */
    public ChatResponseDTO.ReplyMessage chat(Long memberId, ChatRequestDTO.ChatMessageRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 첫 응답에서 <chat_title> 파싱 후 updateChatTitle 로 반영. 생성 시에는 임시 제목만 둔다.
        String provisionalTitle = PROVISIONAL_TITLE;

        // 처음 입력한 채팅 시작
        ChatRoom chatRoom = chatCommandService.createChatRoom(memberId, provisionalTitle, request);
        chatCommandService.createChatMessage(SenderType.USER, MessageType.TEXT, request.message(), chatRoom, memberId, request.imageUrls());

        String normalizedMessage = request.message() == null ? "" : request.message().strip();
        List<ImageFetchResult> images = fetchImagesFromUrls(gcsService.generateDownloadSignedUrl(request.imageUrls()));
        boolean hasImages = !images.isEmpty();

        // 텍스트/이미지 모두 비어있으면 기본 안내 문구 반환
        if (normalizedMessage.isBlank() && !hasImages) {
            String reply = "오늘 하루 어떤 점이 가장 기억에 남으신가요? 한마디라도 괜찮아요.";
            chatCommandService.createChatMessage(SenderType.AI, MessageType.TEXT, reply, chatRoom, memberId, null);
            return ChatConverter.toReplyMessage(reply, chatRoom.getId(), provisionalTitle);
        }

        // 이미지 전용 입력이면 AI가 이미지를 해석할 수 있도록 기본 지시문을 함께 전달
        String userInputForAi = normalizedMessage.isBlank() ? IMAGE_ONLY_PROMPT : normalizedMessage;
        String rawReply = chatWithPrompt(userInputForAi, member.getCharacterType().toString(), images);

        // <chat_title>...</chat_title> 구간을 파싱해 ChatRoom에 저장하고,
        // 사용자에게 보여줄 답변 문자열에서는 해당 토큰을 제거한다.
        ChatTitleParseResult parsed = extractChatTitle(rawReply);
        if (shouldUpdateChatTitle(chatRoom, provisionalTitle, parsed.chatTitle())) {
            chatRoom.updateChatTitle(parsed.chatTitle().trim());
        }

        String visibleReply = parsed.cleanedContent();
        chatCommandService.createChatMessage(SenderType.AI, MessageType.TEXT, visibleReply,chatRoom, memberId, null);
        return ChatConverter.toReplyMessage(visibleReply, chatRoom.getId(), parsed.chatTitle());
    }

    /**
     * http/https URL에서 이미지를 가져옵니다. S3 presigned URL
     */
    private ImageFetchResult fetchImageFromUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            log.warn("허용되지 않은 이미지 URL 스킴: {}", url);
            return null;
        }
        try {
            ResponseEntity<byte[]> response = imageFetchRestTemplate.getForEntity(url, byte[].class);
            if (response.getBody() == null || response.getBody().length == 0) {
                return null;
            }
            if (response.getBody().length > IMAGE_MAX_BYTES) {
                log.warn("이미지 크기 초과(최대 {}MB): {} bytes", IMAGE_MAX_BYTES / 1024 / 1024, response.getBody().length);
                return null;
            }
            String mime = null;
            String contentType = response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
            if (contentType != null) {
                int semicolon = contentType.indexOf(';');
                mime = (semicolon >= 0 ? contentType.substring(0, semicolon) : contentType).strip();
            }
            if (mime == null || !isImageMimeType(mime)) {
                mime = inferMimeTypeFromUrl(url);
            }
            if (mime == null || !isImageMimeType(mime)) {
                log.warn("이미지 MIME 확인 실패(비이미지 응답): {} - contentType={}", url, contentType);
                return null;
            }
            return new ImageFetchResult(response.getBody(), mime);
        } catch (Exception e) {
            log.warn("이미지 URL 다운로드 실패: {} - {}", url, e.getMessage());
            return null;
        }
    }

    private List<ImageFetchResult> fetchImagesFromUrls(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return List.of();
        }

        List<ImageFetchResult> images = new ArrayList<>();
        for (String url : imageUrls) {
            if (url == null || url.isBlank()) {
                continue;
            }
            ImageFetchResult fetched = fetchImageFromUrl(url.strip());
            if (fetched != null && fetched.bytes() != null && fetched.bytes().length > 0) {
                images.add(fetched);
            }
        }
        return images;
    }

    private record ImageFetchResult(byte[] bytes, String mimeType) {}

    private static boolean isImageMimeType(String mime) {
        try {
            return mime != null && MediaType.parseMediaType(mime).getType().equalsIgnoreCase("image");
        } catch (Exception e) {
            return mime != null && mime.toLowerCase().startsWith("image/");
        }
    }

    private static String inferMimeTypeFromUrl(String url) {
        String lower = url.toLowerCase();
        int queryIdx = lower.indexOf('?');
        if (queryIdx >= 0) {
            lower = lower.substring(0, queryIdx);
        }
        if (lower.endsWith(".png")) return MimeTypeUtils.IMAGE_PNG_VALUE;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MimeTypeUtils.IMAGE_JPEG_VALUE;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF_VALUE;
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".bmp")) return "image/bmp";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".heic")) return "image/heic";
        if (lower.endsWith(".heif")) return "image/heif";
        if (lower.endsWith(".avif")) return "image/avif";
        return null;
    }

    /**
     * LLM 응답에서 <chat_title>...</chat_title> 형태의 요약 제목을 추출한다.
     * - chatTitle: 태그 안쪽의 내용 (앞뒤 공백 제거)
     * - cleanedContent: 원본 응답에서 해당 태그 블록을 제거한 문자열
     */
    private ChatTitleParseResult extractChatTitle(String content) {
        if (content == null || content.isBlank()) {
            return new ChatTitleParseResult(null, content);
        }

        String startTag = "<chat_title>";
        String endTag = "</chat_title>";

        int startIdx = content.indexOf(startTag);
        int endIdx = content.indexOf(endTag);

        if (startIdx < 0 || endIdx < 0 || endIdx <= startIdx) {
            return new ChatTitleParseResult(null, content);
        }

        int titleStart = startIdx + startTag.length();
        String title = content.substring(titleStart, endIdx).trim();

        // 태그 블록 전체를 제거한 본문 문자열 구성
        String before = content.substring(0, startIdx);
        String after = content.substring(endIdx + endTag.length());
        String cleaned = (before + after).trim();

        return new ChatTitleParseResult(title, cleaned.isEmpty() ? content : cleaned);
    }

    private record ChatTitleParseResult(String chatTitle, String cleanedContent) {}
    
    // 방의 제목이 없어 "새 대화" 일 때 파싱된 제목을 가져온다.
    private boolean shouldUpdateChatTitle(ChatRoom chatRoom, String provisionalTitle, String parsedTitle) {
        if (parsedTitle == null || parsedTitle.isBlank()) {
            return false;
        }
        String currentTitle = chatRoom.getChatTitle();
        return currentTitle == null || currentTitle.isBlank() || currentTitle.equals(provisionalTitle);
    }

    private String chatWithPrompt(String userMessage, String style, List<ImageFetchResult> images) {
        String resolvedStyle = (style == null || style.isBlank()) ? DEFAULT_STYLE : style.trim().toLowerCase();
        if (!STYLE_PROMPT_PATHS.containsKey(resolvedStyle)) {
            resolvedStyle = DEFAULT_STYLE;
        }
        String systemPrompt = getSystemPrompt(resolvedStyle);
        try {
            if (images != null && !images.isEmpty()) {
                return poomChatClient.prompt()
                        .system(systemPrompt)
                        .user(userSpec -> {
                            userSpec.text(userMessage);
                            for (ImageFetchResult image : images) {
                                userSpec.media(new Media(
                                        MimeTypeUtils.parseMimeType(image.mimeType()),
                                        new ByteArrayResource(image.bytes()))
                                );
                            }
                        })
                        .call()
                        .content();
            }
            return poomChatClient.prompt()
                    .system(systemPrompt)
                    .user(userMessage)
                    .call()
                    .content();
        } catch (Exception e) {
            if (isRateLimitOrQuotaExceeded(e)) {
                throw new GeneralException(GeneralErrorCode.TOO_MANY_REQUESTS);
            }
            StringBuilder causeChain = new StringBuilder();
            for (Throwable c = e; c != null; c = c.getCause()) {
                if (causeChain.length() > 0) causeChain.append(" << ");
                causeChain.append(c.getClass().getSimpleName()).append(": ");
                causeChain.append(c.getMessage() != null ? c.getMessage() : "(null)");
            }
            log.warn("호출 실패: {}", causeChain);
            return "일시적으로 AI 응답을 생성할 수 없습니다.";
        }
    }

    private String getSystemPrompt(String style) {
        return promptCache.computeIfAbsent(style, s -> {
            String path = STYLE_PROMPT_PATHS.get(s);
            try {
                return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.warn("시스템 프롬프트 로드 실패: {} - 기본 프롬프트 사용", path, e);
                try {
                    return new ClassPathResource(STYLE_PROMPT_PATHS.get(DEFAULT_STYLE))
                            .getContentAsString(StandardCharsets.UTF_8);
                } catch (Exception e2) {
                    return "당신은 산후 우울 예방·케어를 돕는 AI 상담 챗봇입니다.";
                }
            }
        });
    }

    /** Gemini 등 AI API 429(한도 초과) / quota / rate limit 여부 확인 */
    private static boolean isRateLimitOrQuotaExceeded(Throwable e) {
        for (Throwable c = e; c != null; c = c.getCause()) {
            String msg = c.getMessage();
            if (msg == null) continue;
            String lower = msg.toLowerCase();
            if (lower.contains("429") || lower.contains("quota") || lower.contains("rate limit")
                    || lower.contains("rate_limit") || lower.contains("too many requests")) {
                return true;
            }
        }
        return false;
    }
}
