package gdg.challenge.poom.domain.auth.dto.request;

import gdg.challenge.poom.domain.member.entity.Gender;

import java.time.LocalDate;

public record AuthRequestDTO() {

    public record SignUp(
            String email,
            String username,
            String password,
            Gender gender,
            String phoneNumber,
            LocalDate birth,
            Long socialId
    ){}
}
