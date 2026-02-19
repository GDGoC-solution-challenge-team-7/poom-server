package gdg.challenge.poom.domain.auth.dto.response;

import gdg.challenge.poom.domain.member.entity.enums.SocialType;
import lombok.Builder;

public record OAuth2ResponseDTO (){

    @Builder
    public record Login(
            String name,
            String email,
            Long socialId,
            boolean isFirst,
            String accessToken,
            String refreshToken
    ) {

    }

    @Builder
    public record GetUserInfo(
            String name,
            String email,
            String providerId,
            SocialType socialType
    ){}
}
