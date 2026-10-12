package gdg.challenge.poom.domain.member.converter;

import gdg.challenge.poom.domain.member.dto.response.MemberResponseDTO;
import gdg.challenge.poom.domain.member.entity.Couple;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.entity.Social;
import gdg.challenge.poom.domain.member.entity.enums.SocialType;
import gdg.challenge.poom.domain.util.MemberStatusUtil;

import java.util.List;

public class MemberConverter {

    public static MemberResponseDTO.MemberInfo toMemberInfo(
            Member member, String profileImageSignedUrl, String socialType
    ){
        return MemberResponseDTO.MemberInfo.builder()
                .name(member.getName())
                .email(member.getEmail())
                .birthDate(member.getBirthDate())
                .gender(member.getGender())
                .userType(member.getUserType())
                .mother(MemberStatusUtil.calcMotherStatus(member))
                .childBirthDate(member.getChildBirthDate())
                .profileImageSignedUrl(profileImageSignedUrl)
                .socialType(socialType)
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

    public static MemberResponseDTO.CoupleConnectionInfo toCoupleConnectionInfo(Couple couple, Member member){
        Member partner = couple.getPartnerOf(member.getId());
        return MemberResponseDTO.CoupleConnectionInfo.builder()
                .coupleId(couple.getId())
                .memberId(member.getId())
                .memberNickname(member.getName())
                .partnerId(partner.getId())
                .partnerNickname(partner.getName())
                .coupleStatus(couple.getCoupleStatus())
                .coupleCode(member.getCoupleCode())
                .build();
    }

}
