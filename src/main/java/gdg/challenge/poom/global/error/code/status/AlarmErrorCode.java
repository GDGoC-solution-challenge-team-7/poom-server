package gdg.challenge.poom.global.error.code.status;

import gdg.challenge.poom.global.error.code.BaseErrorCode;
import gdg.challenge.poom.global.error.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AlarmErrorCode implements BaseErrorCode {
    FCM_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "FCM_500_1", "알림 전송에 실패했습니다."),
    FCM_INVALID_TOKEN(HttpStatus.BAD_REQUEST, "FCM_400_1", "유효하지 않은 FCM 토큰입니다."),
    FCM_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "FCM_404_1", "FCM 토큰을 찾을 수 없습니다."),
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
