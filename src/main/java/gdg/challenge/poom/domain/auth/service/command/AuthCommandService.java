package gdg.challenge.poom.domain.auth.service.command;

import gdg.challenge.poom.domain.auth.converter.AuthConverter;
import gdg.challenge.poom.domain.auth.converter.OAuthConverter;
import gdg.challenge.poom.domain.auth.dto.request.AuthRequestDTO;
import gdg.challenge.poom.domain.auth.dto.response.AuthResponseDTO;
import gdg.challenge.poom.domain.auth.dto.response.OAuth2ResponseDTO;
import gdg.challenge.poom.domain.auth.entity.WithdrawalReasonLog;
import gdg.challenge.poom.domain.auth.entity.enums.WithdrawalReason;
import gdg.challenge.poom.domain.auth.factory.GoogleUserLoader;
import gdg.challenge.poom.domain.auth.service.query.RedisStorageQueryService;
import gdg.challenge.poom.domain.auth.service.query.TokenQueryService;
import gdg.challenge.poom.domain.chat.repository.ChatRoomRepository;
import gdg.challenge.poom.domain.member.converter.MemberConverter;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.entity.Social;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.domain.member.repository.SocialRepository;
import gdg.challenge.poom.domain.member.repository.WithdrawalReasonLogRepository;
import gdg.challenge.poom.domain.member.service.GcsService;
import gdg.challenge.poom.domain.util.MemberFileCollector;
import gdg.challenge.poom.global.error.code.status.AuthErrorCode;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.AuthException;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import gdg.challenge.poom.global.security.constants.AuthenticationConstants;
import gdg.challenge.poom.global.security.domain.CustomUserDetails;
import gdg.challenge.poom.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.util.List;
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
    private final RedisStorageCommandService redisStorageCommandService;
    private final RedisStorageQueryService redisStorageQueryService;
    private final WithdrawalReasonLogRepository withdrawalReasonLogRepository;
    private final MemberFileCollector memberFileCollector;
    private final GcsService gcsService;
    private final JwtUtil jwtUtil;

    public OAuth2ResponseDTO.Login loginWithOAuth(HttpServletRequest request, HttpServletResponse response,
                                                   String code){
        OAuth2ResponseDTO.GetUserInfo userInfo = googleUserLoader.loadUser(code);
        Optional<Social> socialOptional = socialRepository.findByProviderIdAndSocialType(userInfo.providerId(), userInfo.socialType());
        Optional<Member> memberOptional = memberRepository.findByEmail(userInfo.email());

        // 이미 Member가 있는 경우
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            Social social = socialOptional.orElseGet(() ->
                    socialRepository.save(OAuthConverter.toSocial(userInfo, member))
            );
            // jwt 발급해서 넘겨주기
            CustomUserDetails customUserDetails = new CustomUserDetails(memberOptional.get());
            AuthResponseDTO.TokenResult loginToken = tokenCommandService.createLoginToken(customUserDetails);
            redisStorageCommandService.addRefreshToken(member.getId(), loginToken.refreshToken());
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

    public AuthResponseDTO.TokenResult signUp(AuthRequestDTO.SignUp request){
        validateSignUp(request);

        Social social = socialRepository.findById(request.socialId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.SOCIAL_NOT_FOUND));
        Member member = memberRepository.save(AuthConverter.toMember(request));
        member.addSocial(social);
        CustomUserDetails customUserDetails = new CustomUserDetails(member);
        AuthResponseDTO.TokenResult loginToken = tokenCommandService.createLoginToken(customUserDetails);
        redisStorageCommandService.addRefreshToken(member.getId(), loginToken.refreshToken());
        return loginToken;
    }

    public AuthResponseDTO.AccessTokenResult reissue(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = jwtUtil.resolveRefreshToken(request);

        // request Refresh-Token 헤더에 있는 토큰 파싱 가능 여부
        if (refreshToken == null || !jwtUtil.isValid(refreshToken)) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }
        Long memberId = getMemberId(refreshToken);
        String savedRefreshToken = redisStorageQueryService.getRefreshToken(memberId);
        // 기존에 있는 토큰 동일 여부
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        CustomUserDetails customUserDetails = new CustomUserDetails(member);
        String accessToken = tokenCommandService.reissueAccessToken(customUserDetails);
        return AuthConverter.toAccessTokenResult(member.getId(), accessToken);
    }

    private Long getMemberId(String token){
        return tokenQueryService.getMemberId(token);
    }

    private void validateSignUp(AuthRequestDTO.SignUp request){
        if (memberRepository.existsByEmail(request.email())){
            throw new MemberException(MemberErrorCode.ALREADY_EXIST_EMAIL);
        }
    }

    // 로그아웃
    public void logout (HttpServletRequest request, Long memberId){
        String refreshToken = redisStorageQueryService.getRefreshToken(memberId);
        String accessToken = resolveToken(request);
        // 리프레쉬, 액세스 토큰 그냥 싹다 안 됨 처리 - 블랙 리스트 추가, refresh에서 삭제
        redisStorageCommandService.deleteRefreshToken(memberId);
        redisStorageCommandService.addBlackList(refreshToken);
        redisStorageCommandService.addBlackList(accessToken);
    }

    // 탈퇴
    public void withdraw(HttpServletRequest request, Long memberId, AuthRequestDTO.WithdrawRequest withdrawRequest){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        List<String> imageUrls = memberFileCollector.collectByMemberId(memberId);
        if (withdrawRequest.withdrawalReason() != WithdrawalReason.OTHER && withdrawRequest.reasonDescription() != null){
            throw new MemberException(MemberErrorCode.WITHDRAWAL_REASON_CONTENT_NOT_ALLOWED);
        }
        // WithDrawlReason 엔티티 만들기
        WithdrawalReasonLog withdrawalReasonLog = AuthConverter.toWithdrawalReasonLog(withdrawRequest.withdrawalReason(), withdrawRequest.reasonDescription());
        withdrawalReasonLogRepository.save(withdrawalReasonLog);

        // 리프레쉬, 액세스 토큰 그냥 싹다 안 됨 처리 - 블랙 리스트 추가, refresh에서 삭제
        String refreshToken = redisStorageQueryService.getRefreshToken(memberId);
        String accessToken = resolveToken(request);
        redisStorageCommandService.deleteRefreshToken(memberId);
        redisStorageCommandService.addBlackList(refreshToken);
        redisStorageCommandService.addBlackList(accessToken);

        memberRepository.delete(member);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        imageUrls.forEach(gcsService::deleteFile);
                    }
                }
        );
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AuthenticationConstants.AUTH_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(AuthenticationConstants.TOKEN_PREFIX)) {
            return bearerToken.substring(AuthenticationConstants.TOKEN_PREFIX.length());
        }
        return null;
    }

}
