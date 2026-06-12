package gdg.challenge.poom.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import gdg.challenge.poom.global.data.GcsConfigData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class GcsConfig {

    @Bean
    public Storage storage(GcsConfigData gcsConfigData) throws IOException {

        GoogleCredentials credentials;

        if (gcsConfigData.getCredentialsJson() != null && !gcsConfigData.getCredentialsJson().isBlank()) {
            credentials = ServiceAccountCredentials.fromStream(
                    new ByteArrayInputStream(
                                    gcsConfigData.getCredentialsJson().getBytes(StandardCharsets.UTF_8)
                            )
                    );
        } else {
            credentials = GoogleCredentials.getApplicationDefault();
        }

        log.info("credentials = {}", credentials.getClass());
        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId(gcsConfigData.getProjectId())
                .build()
                .getService();
    }
}
