package gdg.challenge.poom.global.error.exception.handler;

import gdg.challenge.poom.global.error.code.BaseErrorCode;
import gdg.challenge.poom.global.error.exception.GeneralException;

public class GcsException extends GeneralException {
    public GcsException(BaseErrorCode code) {
        super(code);
    }
}
