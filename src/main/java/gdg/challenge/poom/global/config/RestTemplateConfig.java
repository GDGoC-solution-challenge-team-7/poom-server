package gdg.challenge.poom.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    private static final int IMAGE_FETCH_CONNECT_TIMEOUT_MS = 5_000;
    private static final int IMAGE_FETCH_READ_TIMEOUT_MS = 15_000;

    @Bean
    @Qualifier("imageFetchRestTemplate")
    public RestTemplate imageFetchRestTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofMillis(IMAGE_FETCH_CONNECT_TIMEOUT_MS))
                .readTimeout(Duration.ofMillis(IMAGE_FETCH_READ_TIMEOUT_MS))
                .build();
    }
}
