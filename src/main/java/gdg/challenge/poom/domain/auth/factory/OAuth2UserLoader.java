package gdg.challenge.poom.domain.auth.factory;

import gdg.challenge.poom.domain.auth.dto.response.OAuth2ResponseDTO;

public interface OAuth2UserLoader {
    OAuth2ResponseDTO.GetUserInfo loadUser(String code);
    String getSocialType();
}
