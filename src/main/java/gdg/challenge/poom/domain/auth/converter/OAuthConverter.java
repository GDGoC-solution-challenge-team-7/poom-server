package gdg.challenge.poom.domain.auth.converter;

import gdg.challenge.poom.domain.auth.dto.response.OAuth2ResponseDTO;
import gdg.challenge.poom.domain.auth.factory.dto.GoogleOAuth2ResponseDTO;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.entity.Social;
import gdg.challenge.poom.domain.member.entity.enums.SocialType;

public class OAuthConverter {

    public static OAuth2ResponseDTO.GetUserInfo toGetUserInfo(GoogleOAuth2ResponseDTO.UserInfo google){
        return OAuth2ResponseDTO.GetUserInfo.builder()
                .name(google.name())
                .email(google.email())
                .providerId(google.id())
                .socialType(SocialType.GOOGLE)
                .build();
    }

    public static Social toSocial(OAuth2ResponseDTO.GetUserInfo userInfo, Member member){
        return Social.builder()
                .socialType(userInfo.socialType())
                .providerId(userInfo.providerId())
                .member(member)
                .build();
    }

    public static Social toSocial(OAuth2ResponseDTO.GetUserInfo userInfo){
        return Social.builder()
                .socialType(userInfo.socialType())
                .providerId(userInfo.providerId())
                .build();
    }

    public static Social toSocial(SocialType socialType,String socialId, Member member){
        return Social.builder()
                .socialType(socialType)
                .providerId(socialId)
                .member(member)
                .build();
    }

    public static Social toSocial(SocialType socialType,String socialId){
        return Social.builder()
                .socialType(socialType)
                .providerId(socialId)
                .build();
    }

    public static OAuth2ResponseDTO.Login toLogin(String name,String email, boolean isFirst, Long socialId,
                                                  String accessToken, String refreshToken) {
        return OAuth2ResponseDTO.Login.builder()
                .name(name)
                .email(email)
                .socialId(socialId)
                .isFirst(isFirst)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
