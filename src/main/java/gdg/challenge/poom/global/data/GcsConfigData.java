package gdg.challenge.poom.global.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix ="spring.cloud.gcp")
public class GcsConfigData {
    private String projectId;
    private Storage storage;
    private String credentialsJson;

    @Getter
    @Setter
    public static class Storage {
        private String bucket;
    }
}
