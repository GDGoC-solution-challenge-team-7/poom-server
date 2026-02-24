package gdg.challenge.poom.global.config;

import com.google.genai.Client;
import gdg.challenge.poom.global.data.GenAiConfigData;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;

/**
 * Google AI Studio API 키만으로 Gemini를 사용하기 위한 수동 설정.
 * (Autoconfig는 project-id/location 시 ADC를 요구하므로 제외하고, API 키 전용 Client를 등록)
 */
@Configuration
@RequiredArgsConstructor
public class GenAiApiKeyConfig {

    private final GenAiConfigData genAiConfigData;

    /**
     * 재시도 없음(1회만 시도). ChatClient·모델 모두 이 빈을 쓰면 메시지 1번 = API 1번.
     */
    @Bean
    public RetryTemplate retryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(1)
                .build();
    }

    @Bean
    public Client genAiClient() {
        return Client.builder()
                .apiKey(genAiConfigData.getApiKey())
                .build();
    }

    @Bean
    public GoogleGenAiChatModel googleGenAiChatModel(Client genAiClient, RetryTemplate retryTemplate) {
        GoogleGenAiChatOptions options = GoogleGenAiChatOptions.builder()
                .model("gemini-3-flash-preview")  // 테스트 엔드포인트와 동일 모델 (gemini-2.0-flash는 무료 한도 0)
                .temperature(0.7)
                .build();
        return GoogleGenAiChatModel.builder()
                .genAiClient(genAiClient)
                .defaultOptions(options)
                .retryTemplate(retryTemplate)
                .build();
    }
}
