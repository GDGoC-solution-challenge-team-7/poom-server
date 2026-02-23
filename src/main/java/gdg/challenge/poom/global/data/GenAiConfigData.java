package gdg.challenge.poom.global.data;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.ai.google.genai")
public class GenAiConfigData {
    private String apiKey;
}
