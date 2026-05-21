package gdg.challenge.poom.global.error.code.status;

import gdg.challenge.poom.global.error.code.BaseErrorCode;
import gdg.challenge.poom.global.error.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JournalErrorCode implements BaseErrorCode {
    JOURNAL_NOT_FOUND(HttpStatus.NOT_FOUND, "JOURNAL_404_1", "해당 일지를 찾을 수 없습니다."),
    JOURNAL_ACCESS_DENIED(HttpStatus.FORBIDDEN, "JOURNAL_403_1", "해당 일지에 접근할 권한이 없습니다."),
    JOURNAL_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "JOURNAL_IMAGE_400_1", "일기 이미지가 최소 1개 필요합니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .httpStatus(status)
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }
}
