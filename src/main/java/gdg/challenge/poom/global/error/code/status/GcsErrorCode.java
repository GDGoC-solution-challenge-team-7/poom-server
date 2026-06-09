package gdg.challenge.poom.global.error.code.status;

import gdg.challenge.poom.global.error.code.BaseErrorCode;
import gdg.challenge.poom.global.error.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GcsErrorCode implements BaseErrorCode {
    INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST, "FILE_400_1", "이미지 파일만 업로드할 수 있습니다."),
    INVALID_EXPERT_PDF_FILE(HttpStatus.BAD_REQUEST, "FILE_400_2", "전문가 인증 파일은 PDF만 업로드할 수 있습니다."),
    INVALID_IMAGE_URL(HttpStatus.BAD_REQUEST, "FILE_400_3", "잘못된 이미지 경로입니다."),
    FILE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "FILE_403_1", "파일에 접근할 권한이 없습니다. 잘못된 이미지 경로입니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE_404_1", "파일을 찾을 수 없습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_500_1", "파일 삭제에 실패했습니다."),
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
