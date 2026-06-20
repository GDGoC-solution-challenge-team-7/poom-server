package gdg.challenge.poom.global.error.code.status;

import gdg.challenge.poom.global.error.code.BaseErrorCode;
import gdg.challenge.poom.global.error.code.ErrorReasonDTO;
import gdg.challenge.poom.global.error.code.ReasonDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
public enum OAuthErrorCode implements BaseErrorCode {

    FAIL_TO_GET_USER_INFO(HttpStatus.INTERNAL_SERVER_ERROR, "OAUTH500_1", "사용자 정보를 가져오는 데 실패했습니다."),
    UNSUPPORTED_SOCIAL_TYPE(HttpStatus.BAD_REQUEST, "OAUTH400_1", "지원하지 않는 소셜 로그인입니다."),
    INVALID_ID_TOKEN(HttpStatus.BAD_REQUEST, "OAUTH400_2", "유효하지 않은 ID Token입니다."),
    GOOGLE_TOKEN_VERIFICATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "OAUTH500_1", "Google Token 검증에 실패했습니다.")
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .httpStatus(this.httpStatus)
                .code(this.code)
                .message(this.message)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .httpStatus(this.httpStatus)
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }
}
