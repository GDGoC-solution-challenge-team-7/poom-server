package gdg.challenge.poom.global.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import gdg.challenge.poom.global.error.code.BaseCode;
import gdg.challenge.poom.global.error.code.status.GeneralSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder()
public class ApiResponse<T> {

    //    @JsonProperty
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(true, GeneralSuccessCode.OK.getCode(),  GeneralSuccessCode.OK.getMessage(), result);
    }

    public static <T> ApiResponse<T> of (BaseCode code, T result){
        return new ApiResponse<>(true, code.getReasonHttpStatus().getCode(), code.getReasonHttpStatus().getMessage(), result);
    }

    public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }
}
