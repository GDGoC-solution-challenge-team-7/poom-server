package gdg.challenge.poom.domain.auth.converter;

import gdg.challenge.poom.domain.auth.dto.request.AuthRequestDTO;
import gdg.challenge.poom.domain.auth.dto.response.AuthResponseDTO;
import gdg.challenge.poom.domain.auth.entity.WithdrawalReasonLog;
import gdg.challenge.poom.domain.auth.entity.enums.WithdrawalReason;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.util.TimeUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class AuthConverter {

    public static AuthResponseDTO.TokenResult toTokenResult(Long memberId, String accessToken, String refreshToken) {
        return AuthResponseDTO.TokenResult.builder()
                .memberId(memberId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static Member toMember(AuthRequestDTO.SignUp dto){
        return Member.builder()
                .name(dto.name())
                .email(dto.email())
                .birthDate(dto.birthDate())
                .gender(dto.gender())
                .userType(dto.userType())
                .childBirthDate(dto.childBirthDate())
                .nextSendAt(TimeUtil.calculateNextSendAt(LocalTime.of(9, 0)))
                .expertiseFile(dto.expertiseFile())
                .hasGivenBirth(dto.hasGivenBirth())
                .build();
    }

    public static AuthResponseDTO.AccessTokenResult toAccessTokenResult(Long memberId, String accessToken) {
        return AuthResponseDTO.AccessTokenResult.builder()
                .memberId(memberId)
                .accessToken(accessToken)
                .build();
    }

    public static WithdrawalReasonLog toWithdrawalReasonLog(WithdrawalReason reason, String detail){
        return WithdrawalReasonLog.builder()
                .reason(reason)
                .build();
    }
}
