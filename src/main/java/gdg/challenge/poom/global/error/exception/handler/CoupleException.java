package gdg.challenge.poom.global.error.exception.handler;

import gdg.challenge.poom.global.error.code.BaseErrorCode;
import gdg.challenge.poom.global.error.exception.GeneralException;

public class CoupleException extends GeneralException {
    public CoupleException(BaseErrorCode code) {
        super(code);
    }
}
