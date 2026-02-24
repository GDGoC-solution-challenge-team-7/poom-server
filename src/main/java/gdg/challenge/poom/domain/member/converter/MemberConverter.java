package gdg.challenge.poom.domain.member.converter;

import gdg.challenge.poom.domain.member.dto.response.MemberResponseDTO;
import gdg.challenge.poom.domain.member.entity.Member;

public class MemberConverter {

    public static MemberResponseDTO.MemberInfo toMemberInfo(Member member){
        return MemberResponseDTO.MemberInfo.builder()
                .name(member.getName())
                .email(member.getEmail())
                .birthDate(member.getBirthDate())
                .gender(member.getGender())
                .userType(member.getUserType())
                .childBirthDueDate(member.getChildBirthDueDate())
                .childBirthDate(member.getChildBirthDate())
                .birthRelationship(member.getBirthRelationship())
                .expertiseFile(member.getExpertiseFile())
                .build();
    }
}
