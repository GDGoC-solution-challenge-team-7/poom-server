package gdg.challenge.poom.domain.auth.factory;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OAuth2UserLoaderFactory {

    private final Map<String, OAuth2UserLoader> oAuth2UserLoaderMap = new ConcurrentHashMap<>();

    public OAuth2UserLoaderFactory(List<OAuth2UserLoader> oAuth2UserLoaders) {
        oAuth2UserLoaders.forEach(
                oAuth2UserLoader -> {
                    String socialType = oAuth2UserLoader.getSocialType().toLowerCase();
                    if (oAuth2UserLoaderMap.get(socialType) != null) {
                        throw new IllegalStateException("OAuth2UserLoader social type 중복: " + socialType);
                    }
                    oAuth2UserLoaderMap.put(socialType, oAuth2UserLoader);
                }
        );
    }

    public OAuth2UserLoader getUserLoader(String provider) {
        return oAuth2UserLoaderMap.get(provider.toLowerCase());
    }
}
