package gdg.challenge.poom.global.error.code.status;

import gdg.challenge.poom.global.error.code.BaseErrorCode;
import gdg.challenge.poom.global.error.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_404_1", "해당 멤버를 찾을 수 없습니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER400_1", "이미 존재하는 이메일입니다."),
    CHILD_BIRTH_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "MEMBER_400_2", "임신 중이거나 자녀를 양육 중인 경우 자녀 생년월일은 필수입니다."),
    MEMBER_NOT_DELETED(HttpStatus.BAD_REQUEST, "MEMBER_400_3", "삭제된 사용자가 아닙니다."),
    ACCOUNT_RECOVERY_PERIOD_EXPIRED(HttpStatus.BAD_REQUEST, "MEMBER_400_4", "계정 복구 가능 기간이 지났습니다."),
    SOCIAL_NOT_FOUND(HttpStatus.NOT_FOUND, "SOCIAL_404_1", "해당 소셜를 찾을 수 없습니다."),
    WITHDRAWAL_REASON_CONTENT_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "WITHDRAWAL_400_1", "탈퇴 사유가 OTHERS가 아닌 경우 내용을 입력할 수 없습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return gdg.challenge.poom.global.error.code.ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return gdg.challenge.poom.global.error.code.ErrorReasonDTO.builder()
                .httpStatus(status)
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }
}
