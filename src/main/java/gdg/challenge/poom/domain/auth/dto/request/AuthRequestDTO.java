package gdg.challenge.poom.domain.auth.dto.request;

import gdg.challenge.poom.domain.auth.entity.enums.WithdrawalReason;
import gdg.challenge.poom.domain.member.entity.enums.Gender;
import gdg.challenge.poom.domain.member.entity.enums.BirthRelationship;
import gdg.challenge.poom.domain.member.entity.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AuthRequestDTO() {

    public record SignUp(
            @NotBlank @Size(min = 2)
            String name,
            @Email @NotBlank
            String email,
            @NotNull
            LocalDate birthDate,
            @NotNull
            Gender gender,
            @NotNull
            Long socialId,
            @NotNull
            UserType userType,
            LocalDate childBirthDate,
            BirthRelationship birthRelationship,
            String expertiseFile,
            Boolean hasGivenBirth
    ){}

    public record WithdrawRequest(
            @NotNull
            WithdrawalReason withdrawalReason,
            String reasonDescription
    ){}

    public record TokenRequest(
            String idToken
    ){}
}
