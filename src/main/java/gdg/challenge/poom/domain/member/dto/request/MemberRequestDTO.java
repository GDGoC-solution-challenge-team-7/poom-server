package gdg.challenge.poom.domain.member.dto.request;

import gdg.challenge.poom.domain.member.entity.enums.Gender;
import gdg.challenge.poom.domain.member.entity.enums.BirthRelationship;
import gdg.challenge.poom.domain.member.entity.enums.UserType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MemberRequestDTO() {

    public record ChangeMemberInfo(
            @NotNull
            String name,
            @NotNull
            String email,
            @NotNull
            LocalDate birthDate,
            @NotNull
            Gender gender,
            @NotNull
            UserType userType,
            LocalDate childBirthDueDate,
            LocalDate childBirthDate,
            BirthRelationship birthRelationship,
            String expertiseFile
    ){ }

    // 파일 업로드 요청
    public record SignedUrlRequest(
            String filename,
            String contentType
    ){}
}
