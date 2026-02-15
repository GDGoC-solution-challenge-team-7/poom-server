package gdg.challenge.poom.domain.auth.converter;

import gdg.challenge.poom.domain.auth.dto.response.AuthResponseDTO;
import gdg.challenge.poom.domain.auth.dto.response.OAuth2ResponseDTO;
import gdg.challenge.poom.domain.member.entity.Member;

public class AuthConverter {

    public static AuthResponseDTO.TokenResult toTokenResult(Long memberId, String accessToken, String refreshToken) {
        return AuthResponseDTO.TokenResult.builder()
                .memberId(memberId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static Member toMember(OAuth2ResponseDTO.GetUserInfo userInfo){
        return Member.builder()
                .email(userInfo.email())
                .build();
    }

    public static AuthResponseDTO.AccessTokenResult toAccessTokenResult(Long memberId, String accessToken) {
        return AuthResponseDTO.AccessTokenResult.builder()
                .memberId(memberId)
                .accessToken(accessToken)
                .build();
    }
}
