package gdg.challenge.poom.domain.member.service;

import gdg.challenge.poom.domain.member.dto.request.MemberRequestDTO;
import gdg.challenge.poom.domain.member.dto.response.MemberResponseDTO;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.repository.MemberRepository;
import gdg.challenge.poom.global.error.code.status.MemberErrorCode;
import gdg.challenge.poom.global.error.exception.handler.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final GcsService gcsService;

    public void changeMemberInfo(Long memberId, MemberRequestDTO.ChangeMemberInfo request){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        member.changeMemberInfo(request);
    }

    public MemberResponseDTO.SignedUrlResponse uploadMemberProfileImage(Long memberId, MemberRequestDTO.SignedUrlRequest request){
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        if (member.getProfileImage() != null) {
            gcsService.deleteFile(member.getProfileImage());
        }
        member.updateProfileImage(request.filename());
        return gcsService.generateUploadSignedUrl(memberId, request);
    }

}
