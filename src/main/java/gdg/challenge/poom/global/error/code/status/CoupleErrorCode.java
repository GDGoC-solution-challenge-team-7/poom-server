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
    SAME_MEMBER_CONNECTION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "COUPLE_400_1", "같은 회원끼리는 연결할 수 없습니다.");


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
