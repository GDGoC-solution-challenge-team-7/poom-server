package gdg.challenge.poom.service;

import gdg.challenge.poom.global.error.code.status.GeneralErrorCode;
import gdg.challenge.poom.global.error.exception.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    /** style -> 시스템 프롬프트 파일 경로 (prompts/ 하위) */
    private static final Map<String, String> STYLE_PROMPT_PATHS = Map.of(
            "empathy", "prompts/poom-system-empathy.txt",
            "solution", "prompts/poom-system-solution.txt"
    );

    private static final String DEFAULT_STYLE = "empathy"; // 기본 AI 응답 스타일

    private final ChatClient poomChatClient;
    private final Map<String, String> promptCache = new ConcurrentHashMap<>();

    public ChatService(@Qualifier("poomChatClient") ChatClient poomChatClient) {
        this.poomChatClient = poomChatClient;
    }

    /**
     * 사용자 메시지와 스타일(공감/해결/균형)에 따라 AI 응답을 반환합니다.
     * 채팅 1회당 Gemini API 1회만 호출 (재시도·딜레이 없음).
     *
     * @param userMessage 사용자 메시지
     * @param style       "empathy"(공감 우선, 기본), "solution"(해결·실천 우선). null/빈값이면 empathy.
     */
    public String chat(String userMessage, String style) {
        if (userMessage == null || userMessage.isBlank()) {
            return "오늘 하루 어떤 점이 가장 기억에 남으신가요? 한마디라도 괜찮아요.";
        }
        String resolvedStyle = (style == null || style.isBlank()) ? DEFAULT_STYLE : style.trim().toLowerCase();
        if (!STYLE_PROMPT_PATHS.containsKey(resolvedStyle)) {
            resolvedStyle = DEFAULT_STYLE;
        }
        String systemPrompt = getSystemPrompt(resolvedStyle);
        try {
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
