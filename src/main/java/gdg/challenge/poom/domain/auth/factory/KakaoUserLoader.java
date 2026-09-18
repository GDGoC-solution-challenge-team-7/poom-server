package gdg.challenge.poom.domain.auth.factory;

import gdg.challenge.poom.domain.auth.converter.OAuthConverter;
import gdg.challenge.poom.domain.auth.dto.response.OAuth2ResponseDTO;
import gdg.challenge.poom.domain.auth.factory.dto.KakaoOAuth2ResponseDTO;
import gdg.challenge.poom.domain.member.entity.enums.SocialType;
import gdg.challenge.poom.global.data.OAuth2ConfigData;
import gdg.challenge.poom.global.security.constants.AuthenticationConstants;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class KakaoUserLoader extends AbstractOAuth2UserLoader{

    private static final SocialType SOCIAL_TYPE = SocialType.KAKAO;

    public KakaoUserLoader(OAuth2ConfigData oAuth2ConfigData) {
        super(oAuth2ConfigData);
    }

    @Override
    protected String getAccessToken(String code) throws IOException {
        KakaoOAuth2ResponseDTO.Token token = super.getToken(code, KakaoOAuth2ResponseDTO.Token.class);
        return token.access_token();
    }

    @Override
    protected OAuth2ResponseDTO.GetUserInfo getUserInfo(String token) throws IOException {
        KakaoOAuth2ResponseDTO.KakaoProfile profile = super.getProfile(AuthenticationConstants.TOKEN_PREFIX, token, KakaoOAuth2ResponseDTO.KakaoProfile.class);
        return OAuthConverter.toGetUserInfo(profile);
    }

    @Override
    public String getSocialType() {
        return SOCIAL_TYPE.name().toLowerCase();
    }
}
