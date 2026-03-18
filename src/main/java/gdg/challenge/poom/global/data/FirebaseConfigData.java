package gdg.challenge.poom.global.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.firebase")
public class FirebaseConfigData {
    private boolean enabled = false;
    private String config;
}
