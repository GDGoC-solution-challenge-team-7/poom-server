package gdg.challenge.poom.global.error.code.status;

import gdg.challenge.poom.global.error.code.BaseCode;
import gdg.challenge.poom.global.error.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralSuccessCode implements BaseCode {

    OK(HttpStatus.OK, "COMMON200_1", "성공한 요청입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .httpStatus(status)
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }
}
