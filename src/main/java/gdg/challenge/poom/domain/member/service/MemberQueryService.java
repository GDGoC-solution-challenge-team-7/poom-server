package gdg.challenge.poom.domain.member.service;

import gdg.challenge.poom.domain.auth.service.query.TokenQueryService;
import gdg.challenge.poom.domain.member.converter.MemberConverter;
import gdg.challenge.poom.domain.member.dto.response.MemberResponseDTO;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.repository.CoupleRepository;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import gdg.challenge.poom.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberRepository memberRepository;
    private final CoupleRepository coupleRepository;
    private final TokenQueryService tokenQueryService;
    private final GcsService gcsService;
    private final JwtUtil jwtUtil;

    public Member findById(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public MemberResponseDTO.MemberInfo getMemberInfo(HttpServletRequest request, Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        String token = jwtUtil.resolveToken(request);
        String socialType = tokenQueryService.getSocialType(token);
        String profileImageSignedUrl = null;
        if (member.getProfileImage() != null){
            profileImageSignedUrl = gcsService.generateDownloadSignedUrl(member.getProfileImage());
        }
        return  MemberConverter.toMemberInfo(member, profileImageSignedUrl, socialType);
    }

    public MemberResponseDTO.CoupleConnectionInfo getCoupleConnectionInfo(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        return coupleRepository.getCoupleByMemberId(memberId)
                .map(c -> MemberConverter.toCoupleConnectionInfo(c, member))
                .orElseGet(() -> MemberConverter.toCoupleConnectionInfo(member));
    }
}
