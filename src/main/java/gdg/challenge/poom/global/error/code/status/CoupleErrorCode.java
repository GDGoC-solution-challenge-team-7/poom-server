package gdg.challenge.poom.global.error.code.status;

import gdg.challenge.poom.global.error.code.BaseErrorCode;
import gdg.challenge.poom.global.error.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CoupleErrorCode implements BaseErrorCode {
    MEMBER_NOT_FOUND_BY_CODE(HttpStatus.NOT_FOUND, "COUPLE_404_1", "해당 연결 코드에 해당하는 멤버가 없습니다."),
    COUPLE_NOT_FOUND_BY_MEMBER(HttpStatus.NOT_FOUND, "COUPLE_404_2", "해당 멤버가 속한 커플을 찾을 수 없습니다."),
    SAME_MEMBER_CONNECTION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "COUPLE_400_1", "같은 회원끼리는 연결할 수 없습니다."),
    COUPLE_REJOIN_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "COUPLE_400_2", "부부 연결을 복구할 수 없습니다."),
    NEW_CONNECTION_NOT_ALLOWED_DURING_REJOIN_PERIOD(HttpStatus.CONFLICT, "COUPLE_409_1", "기존 부부 연결의 복구 유예기간에는 새로운 연결을 할 수 없습니다."),
    MEMBER_ALREADY_CONNECTED(HttpStatus.CONFLICT, "COUPLE_409_2", "이미 연결된 상대가 있어 새로운 부부 연결을 할 수 없습니다."),
    NOT_IN_DISCONNECTED_GRACE_PERIOD(HttpStatus.CONFLICT, "COUPLE_409_3", "현재 연결 복구 유예기간 상태가 아닙니다."),
    DISCONNECTED_GRACE_PERIOD_EXPIRED(HttpStatus.CONFLICT, "COUPLE_409_4", "연결 복구 유예기간이 만료되었습니다.")
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
