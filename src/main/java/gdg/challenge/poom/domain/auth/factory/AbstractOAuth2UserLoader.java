package gdg.challenge.poom.domain.auth.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import gdg.challenge.poom.domain.auth.dto.response.OAuth2ResponseDTO;
import gdg.challenge.poom.global.data.OAuth2ConfigData;
import gdg.challenge.poom.global.error.code.status.OAuthErrorCode;
import gdg.challenge.poom.global.error.exception.handler.OAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractOAuth2UserLoader implements OAuth2UserLoader {

    private final OAuth2ConfigData oAuth2ConfigData;

    @Override
    public OAuth2ResponseDTO.GetUserInfo loadUser(String code) {
        try {
            // Authorization code로 토큰 받기
            String token = getAccessToken(code);
            // code로 UserInfo받기
            return getUserInfo(token);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new OAuthException(OAuthErrorCode.FAIL_TO_GET_USER_INFO);
        }
    }

    protected abstract String getAccessToken(String code) throws IOException;

    protected abstract OAuth2ResponseDTO.GetUserInfo getUserInfo(String token) throws IOException;

    protected <T> T getToken(String code, Class<T> clz) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", getClientId());
        params.add("redirect_uri", getRedirectUri());
        params.add("code", code);
        Optional.ofNullable(getClientSecret()).ifPresent(secret -> params.add("client_secret", secret));
        HttpEntity<MultiValueMap> request = new HttpEntity<>(params, httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getTokenUri(),
                HttpMethod.POST,
                request,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(response.getBody(), clz);
    }

    protected <T> T getProfile(String tokenPrefix, String token, Class<T> clz) throws IOException {
        // 토큰으로 정보 가져오기
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("Authorization", tokenPrefix + token);
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap> request1 = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getUserInfoUri(),
                HttpMethod.GET,
                request1,
                String.class
        );

        ObjectMapper om = new ObjectMapper();

        return om.readValue(response.getBody(), clz);
    }

    protected String getClientId() {
        return this.oAuth2ConfigData.getRegistration().get(this.getSocialType()).getClientId();
    }

    protected String getClientSecret() {
        return this.oAuth2ConfigData.getRegistration().get(this.getSocialType()).getClientSecret();
    }

    protected String getRedirectUri() {
        return this.oAuth2ConfigData.getRegistration().get(this.getSocialType()).getRedirectUri();
    }

    protected String getTokenUri() {
        return this.oAuth2ConfigData.getProvider().get(this.getSocialType()).getTokenUri();
    }

    protected String getUserInfoUri() {
        return this.oAuth2ConfigData.getProvider().get(this.getSocialType()).getUserInfoUri();
    }

}
