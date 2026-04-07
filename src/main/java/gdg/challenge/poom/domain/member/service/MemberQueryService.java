package gdg.challenge.poom.domain.member.service;

import gdg.challenge.poom.domain.member.converter.MemberConverter;
import gdg.challenge.poom.domain.member.dto.response.MemberResponseDTO;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberRepository memberRepository;
    private final GcsService gcsService;

    public Member findById(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public MemberResponseDTO.MemberInfo getMemberInfo(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        String profileImageSignedUrl = null;
        if (member.getProfileImage() != null){
            profileImageSignedUrl = gcsService.generateDownloadSignedUrl(member.getProfileImage());
        }
        String expertiseFile = null;
        if (member.getExpertiseFile() != null){
            expertiseFile = gcsService.generateDownloadSignedUrl(member.getExpertiseFile());
        }
        return  MemberConverter.toMemberInfo(member, profileImageSignedUrl, expertiseFile);
    }

}
