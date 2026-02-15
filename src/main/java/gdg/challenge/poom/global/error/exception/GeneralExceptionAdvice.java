package gdg.challenge.poom.global.error.exception;

import gdg.challenge.poom.global.error.ApiResponse;
import gdg.challenge.poom.global.error.code.BaseErrorCode;
import gdg.challenge.poom.global.error.code.status.GeneralErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GeneralExceptionAdvice {

    private static boolean isRateLimitOrQuotaExceeded(Throwable e) {
        for (Throwable c = e; c != null; c = c.getCause()) {
            String msg = c.getMessage();
            if (msg == null) continue;
            String lower = msg.toLowerCase();
            if (lower.contains("429") || lower.contains("quota") || lower.contains("rate limit")
                    || lower.contains("rate_limit") || lower.contains("too many requests")) {
                return true;
            }
        }
        return false;
    }

    // 애플리케이션에서 발생하는 커스텀 예외를 처리
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Void>> handException(GeneralException ex){
        return ResponseEntity.status(ex.getCode().getReasonHttpStatus().getHttpStatus())
                .body(ApiResponse.onFailure(ex.getErrorReason().getCode(),ex.getErrorReason().getMessage(), null));
    }

    // AI API 429(한도 초과) 등 — 메시지/원인 체인에 429·quota·rate limit 포함 시
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
        if (isRateLimitOrQuotaExceeded(ex)) {
            BaseErrorCode code = GeneralErrorCode.TOO_MANY_REQUESTS;
            return ResponseEntity
                    .status(code.getReasonHttpStatus().getHttpStatus())
                    .body(ApiResponse.onFailure(code.getReason().getCode(), code.getReason().getMessage(), null));
        }
        BaseErrorCode code = GeneralErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(code.getReasonHttpStatus().getHttpStatus())
                .body(ApiResponse.onFailure(code.getReason().getCode(), ex.getMessage(), null));
    }

    // 컨트롤러 메서드에서 @Valid 어노테이션을 사용하여 DTO의 유효성 검사를 수행
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        // 검사에 실패한 필드와 그에 대한 메시지를 저장하는 Map
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        GeneralErrorCode code = GeneralErrorCode.VALID_FAIL;
        ApiResponse<Map<String, String>> errorResponse = ApiResponse.onFailure(code.getReason().getCode(), code.getReason().getMessage(), errors);

        // 에러 코드, 메시지와 함께 errors를 반환
        return ResponseEntity.status(code.getStatus()).body(errorResponse);
    }
}
