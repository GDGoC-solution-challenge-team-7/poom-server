package gdg.challenge.poom.domain.auth.dto.response;

import lombok.Builder;

public record AuthResponseDTO() {

    @Builder
    public record TokenResult(
            Long memberId,
            String accessToken,
            String refreshToken
    ){}

    @Builder
    public record AccessTokenResult(
            Long memberId,
            String accessToken
    ){

    }
}
