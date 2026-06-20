package gdg.challenge.poom.global.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import gdg.challenge.poom.domain.auth.factory.GoogleUserLoader;
import gdg.challenge.poom.domain.member.entity.enums.SocialType;
import gdg.challenge.poom.global.data.OAuth2ConfigData;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class GoogleOauthConfig {

    private final GoogleUserLoader googleUserLoader;

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier() {
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleUserLoader.getClientId()))
                .build();
    }
}
