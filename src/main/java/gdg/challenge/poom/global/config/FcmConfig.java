package gdg.challenge.poom.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import gdg.challenge.poom.global.data.FirebaseConfigData;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FcmConfig {

    private final FirebaseConfigData firebaseConfigData;
    @PostConstruct
    public void initialize() {
        try {
            if (firebaseConfigData.isEnabled()) {
                ByteArrayInputStream serviceAccount = new ByteArrayInputStream(firebaseConfigData.getConfig().getBytes());

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("FCM 초기화 실패", e);
        }
    }
}
