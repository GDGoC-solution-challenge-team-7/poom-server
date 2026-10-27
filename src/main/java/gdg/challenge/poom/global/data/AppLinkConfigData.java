package gdg.challenge.poom.global.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app-link")
public class AppLinkConfigData {
    private Android android = new Android();

    @Getter
    @Setter
    public static class Android {
        private String packageName;
        private List<String> sha256CertFingerprints;
    }
}
