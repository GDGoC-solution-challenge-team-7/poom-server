package gdg.challenge.poom.domain.member.dto.response;

import gdg.challenge.poom.domain.member.entity.Gender;
import gdg.challenge.poom.domain.member.entity.enums.BirthRelationship;
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
            LocalDate childBirthDueDate,
            LocalDate childBirthDate,
            BirthRelationship birthRelationship,
            String expertiseFile
    ){}
}
