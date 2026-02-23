package gdg.challenge.poom.domain.chat.service;

import gdg.challenge.poom.domain.chat.converter.ChatConverter;
import gdg.challenge.poom.domain.chat.dto.request.ChatRequestDTO;
import gdg.challenge.poom.domain.chat.dto.response.ChatResponseDTO;
import gdg.challenge.poom.global.error.code.status.GeneralErrorCode;
import gdg.challenge.poom.global.error.exception.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatHelperService {

    private static final Logger log = LoggerFactory.getLogger(ChatHelperService.class);
    private static final int IMAGE_FETCH_CONNECT_TIMEOUT_MS = 5_000;
    private static final int IMAGE_FETCH_READ_TIMEOUT_MS = 15_000;
    private static final long IMAGE_MAX_BYTES = 10 * 1024 * 1024; // 10MB

    /** style -> 시스템 프롬프트 파일 경로 (prompts/ 하위) */
    private static final Map<String, String> STYLE_PROMPT_PATHS = Map.of(
            "empathy", "prompts/poom-system-empathy.txt",
            "solution", "prompts/poom-system-solution.txt"
    );

    private static final String DEFAULT_STYLE = "empathy"; // 기본 AI 응답 스타일

    private final ChatClient poomChatClient;
    private final RestTemplate imageFetchRestTemplate;
    private final Map<String, String> promptCache = new ConcurrentHashMap<>();

    public ChatHelperService(
            @Qualifier("poomChatClient") ChatClient poomChatClient,
            RestTemplateBuilder restTemplateBuilder) {
        this.poomChatClient = poomChatClient;
        this.imageFetchRestTemplate = restTemplateBuilder
                .connectTimeout(java.time.Duration.ofMillis(IMAGE_FETCH_CONNECT_TIMEOUT_MS))
                .readTimeout(java.time.Duration.ofMillis(IMAGE_FETCH_READ_TIMEOUT_MS))
                .build();
    }

    /**
     * 사용자 메시지와 스타일에 따라 AI 응답을 반환합니다.
     * imageUrl이 있으면 해당 URL에서 이미지를 가져옵니다.
     *
     *  imageUrl 선택. 이미지 URL (http/https, S3 presigned URL 등)
     */
    public ChatResponseDTO.ReplyMessage chat(ChatRequestDTO.ChatMessageRequest request) {

        // TODO: 사용자가 처음에 입력한 채팅을 기준으로 제목 생성
        String title = "";

        // TODO: 채팅방 생성, 처음
//        ChatConverter.toChatRoom()


        
        // TODO: 채팅 메시지 저장

        if (request.message() == null || request.message().isBlank()) {
            String reply = "오늘 하루 어떤 점이 가장 기억에 남으신가요? 한마디라도 괜찮아요.";
            // TODO: 채팅 메시지 저장
            return ChatConverter.toReplyMessage(reply);
        }
        byte[] imageBytes = null;
        String mime = "image/jpeg";

        if (request.imageUrl() != null && !request.imageUrl().isBlank()) {
            var fetched = fetchImageFromUrl(request.imageUrl().strip());
            if (fetched != null) {
                imageBytes = fetched.bytes();
                if (fetched.mimeType() != null) {
                    mime = fetched.mimeType();
                }
            }
        }

        String reply = chatWithPrompt(request.message(), request.characterType().toString(), imageBytes, mime);
        // TODO: 채팅 메시지 저장
        return ChatConverter.toReplyMessage(reply);
    }

    /**
     * http/https URL에서 이미지를 가져옵니다. S3 presigned URL 등 지원.
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
            if (mime == null || !mime.startsWith("image/")) {
                mime = "image/jpeg";
            }
            return new ImageFetchResult(response.getBody(), mime);
        } catch (Exception e) {
            log.warn("이미지 URL 다운로드 실패: {} - {}", url, e.getMessage());
            return null;
        }
    }

    private record ImageFetchResult(byte[] bytes, String mimeType) {}

    private String chatWithPrompt(String userMessage, String style, byte[] imageBytes, String imageMimeType) {
        String resolvedStyle = (style == null || style.isBlank()) ? DEFAULT_STYLE : style.trim().toLowerCase();
        if (!STYLE_PROMPT_PATHS.containsKey(resolvedStyle)) {
            resolvedStyle = DEFAULT_STYLE;
        }
        String systemPrompt = getSystemPrompt(resolvedStyle);
        try {
            if (imageBytes != null && imageBytes.length > 0) {
                var systemMessage = new SystemMessage(systemPrompt);
                var media = new Media(MimeTypeUtils.parseMimeType(imageMimeType), new ByteArrayResource(imageBytes));
                var userMsg = UserMessage.builder()
                        .text(userMessage)
                        .media(media)
                        .build();
                return poomChatClient.prompt()
                        .messages(List.of(systemMessage, userMsg))
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
