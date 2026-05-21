package gdg.challenge.poom.domain.member.dto.request;

import gdg.challenge.poom.domain.chat.entity.enums.UploadDomain;
import gdg.challenge.poom.domain.member.entity.enums.BirthRelationship;
import gdg.challenge.poom.domain.member.entity.enums.Gender;
import gdg.challenge.poom.domain.member.entity.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

public record MemberRequestDTO() {

    public record ChangeMemberInfo(
            @NotBlank(message = "이름은 필수 입력입니다.")
            String name,
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            @NotBlank(message = "이메일은 필수입니다.")
            String email,
            @NotNull(message = "생년월일은 필수 입력입니다.")
            LocalDate birthDate,
            @NotNull(message = "성별은 필수 선택입니다.")
            Gender gender,
            @NotNull(message = "유저 타입은 필수 선택입니다.")
            UserType userType,
            LocalDate childBirthDate,
            BirthRelationship birthRelationship,
            String expertiseFile
    ){ }

    // 파일 업로드 요청 - 단건
    public record SignedUrlRequest(
            @NotBlank
            String filename,
            @NotBlank
            String contentType,
            @NotNull
            UploadDomain domain
    ){}

    // 파일 업로드 요청 - 여러건
    public record SignedUrlBatchRequest(
            @NotEmpty
            List<SignedUrlRequest> files
    ){}

}
