package gdg.challenge.poom.domain.auth.service.command;

import gdg.challenge.poom.domain.auth.converter.AuthConverter;
import gdg.challenge.poom.domain.auth.converter.OAuthConverter;
import gdg.challenge.poom.domain.auth.dto.request.AuthRequestDTO;
import gdg.challenge.poom.domain.auth.dto.response.AuthResponseDTO;
import gdg.challenge.poom.domain.auth.dto.response.OAuth2ResponseDTO;
import gdg.challenge.poom.domain.auth.factory.GoogleUserLoader;
import gdg.challenge.poom.domain.auth.service.query.TokenQueryService;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.entity.Social;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.domain.member.repository.SocialRepository;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import gdg.challenge.poom.global.security.domain.CustomUserDetails;
import gdg.challenge.poom.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandService {

    private final GoogleUserLoader googleUserLoader;
    private final SocialRepository socialRepository;
    private final MemberRepository memberRepository;
    private final TokenCommandService tokenCommandService;
    private final TokenQueryService tokenQueryService;

    public OAuth2ResponseDTO.Login loginWithOAuth(HttpServletRequest request, HttpServletResponse response,
                                                   String code){
        OAuth2ResponseDTO.GetUserInfo userInfo = googleUserLoader.loadUser(code);
        Optional<Social> socialOptional = socialRepository.findByProviderIdAndSocialType(userInfo.providerId(), userInfo.socialType());
        Optional<Member> memberOptional = memberRepository.findByEmail(userInfo.email());

        // 이미 Member가 있는 경우
        if (memberOptional.isPresent()) {
            Social social = socialOptional.orElseGet(() ->
                    socialRepository.save(OAuthConverter.toSocial(userInfo, memberOptional.get()))
            );
            // jwt 발급해서 넘겨주기
            CustomUserDetails customUserDetails = new CustomUserDetails(memberOptional.get());
            AuthResponseDTO.TokenResult loginToken = tokenCommandService.createLoginToken(customUserDetails);
            // response 형식에 맞춰서 주기
            return OAuthConverter.toLogin(userInfo.name(), userInfo.email(), false, social.getId(), loginToken.accessToken(), loginToken.refreshToken());
        }
        // 회원가입이 안 된 경우
        else {
            Social social = socialOptional.orElseGet(() ->
                    socialRepository.save(OAuthConverter.toSocial(userInfo))
            );
            return OAuthConverter.toLogin(userInfo.name(), userInfo.email(), true, social.getId(), null, null);
        }
    }

    public AuthResponseDTO.AccessTokenResult reissue(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = JwtUtil.resolveToken(request);
        Long memberId = getMemberId(refreshToken);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        CustomUserDetails customUserDetails = new CustomUserDetails(member);
        String accessToken = tokenCommandService.reissueAccessToken(customUserDetails);
        return AuthConverter.toAccessTokenResult(member.getId(), accessToken);
    }

    private Long getMemberId(String token){
        return tokenQueryService.getMemberId(token);
    }

    // 로그아웃

}
