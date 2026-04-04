package gdg.challenge.poom.domain.auth.dto.request;

import gdg.challenge.poom.domain.member.entity.enums.Gender;
import gdg.challenge.poom.domain.member.entity.enums.BirthRelationship;
import gdg.challenge.poom.domain.member.entity.enums.UserType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AuthRequestDTO() {

    public record SignUp(
            @NotNull
            String name,
            @NotNull
            String email,
            @NotNull
            LocalDate birthDate,
            @NotNull
            Gender gender,
//            String phoneNumber,
//            Integer Age,
            @NotNull
            Long socialId,
            @NotNull
            UserType userType,
            LocalDate childBirthDate,
            BirthRelationship birthRelationship,
            String expertiseFile,
            Boolean hasGivenBirth
    ){}
}
