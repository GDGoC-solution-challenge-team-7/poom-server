package gdg.challenge.poom.domain.member.dto.response;

import gdg.challenge.poom.domain.member.entity.enums.Gender;
import gdg.challenge.poom.domain.member.entity.enums.BirthRelationship;
import gdg.challenge.poom.domain.member.entity.enums.Mother;
import gdg.challenge.poom.domain.member.entity.enums.UserType;
import lombok.Builder;

import java.time.LocalDate;

public record MemberResponseDTO() {

    @Builder
    public record MemberInfo(
            String name,
            String email,
            LocalDate birthDate,
            Gender gender,
            UserType userType,
            Mother mother,
            LocalDate childBirthDate,
            BirthRelationship birthRelationship,
            String expertiseFile
    ){}

    // 업로드된 파일 응답
    @Builder
    public record SignedUrlResponse(
            String objectName,
            String signedUrl,
            String publicUrl
    ){ }

}
