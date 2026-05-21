package gdg.challenge.poom.global.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import gdg.challenge.poom.global.data.GcsConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GcsConfig {

    @Bean
    public Storage storage(GcsConfigData gcsConfigData) {
        return StorageOptions.newBuilder()
                .setProjectId(gcsConfigData.getProjectId())
                .build()
                .getService();
    }
}
