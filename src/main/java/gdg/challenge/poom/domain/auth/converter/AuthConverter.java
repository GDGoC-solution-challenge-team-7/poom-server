package gdg.challenge.poom.domain.auth.converter;

import gdg.challenge.poom.domain.auth.dto.request.AuthRequestDTO;
import gdg.challenge.poom.domain.auth.dto.response.AuthResponseDTO;
import gdg.challenge.poom.domain.member.entity.Member;

public class AuthConverter {

    public static AuthResponseDTO.TokenResult toTokenResult(Long memberId, String accessToken, String refreshToken) {
        return AuthResponseDTO.TokenResult.builder()
                .memberId(memberId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static Member toMember(AuthRequestDTO.SignUp dto){
        return Member.builder()
                .name(dto.name())
                .email(dto.email())
                .birthDate(dto.birthDate())
                .gender(dto.gender())
                .userType(dto.userType())
                .childBirthDate(dto.childBirthDate())
                .expertiseFile(dto.expertiseFile())
                .hasGivenBirth(dto.hasGivenBirth())
                .build();
    }

    public static AuthResponseDTO.AccessTokenResult toAccessTokenResult(Long memberId, String accessToken) {
        return AuthResponseDTO.AccessTokenResult.builder()
                .memberId(memberId)
                .accessToken(accessToken)
                .build();
    }
}
