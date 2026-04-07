package gdg.challenge.poom.domain.member.converter;

import gdg.challenge.poom.domain.member.dto.response.MemberResponseDTO;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.util.MemberStatusUtil;

import java.util.List;

public class MemberConverter {

    public static MemberResponseDTO.MemberInfo toMemberInfo(Member member){
        return MemberResponseDTO.MemberInfo.builder()
                .name(member.getName())
                .email(member.getEmail())
                .birthDate(member.getBirthDate())
                .gender(member.getGender())
                .userType(member.getUserType())
                .mother(MemberStatusUtil.calcMotherStatus(member))
                .childBirthDate(member.getChildBirthDate())
                .birthRelationship(member.getBirthRelationship())
                .expertiseFile(member.getExpertiseFile())
                .build();
    }

    // MemberResponseDTO.SignedUrlResponse
    public static MemberResponseDTO.SignedUrlResponse toSignedUrlResponse(
            String objectName, String signedUrl, String publicUrl
    ){
        return MemberResponseDTO.SignedUrlResponse.builder()
                .objectName(objectName)
                .signedUrl(signedUrl)
                .publicUrl(publicUrl)
                .build();
    }

    // MemberResponseDTO.SignedUrlBatchResponse
    public static MemberResponseDTO.SignedUrlBatchResponse toSignedUrlResponse(
            List<MemberResponseDTO.SignedUrlResponse> signedUrls
    ){
        return MemberResponseDTO.SignedUrlBatchResponse.builder()
                .files(signedUrls)
                .build();
    }
}
