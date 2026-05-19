package gdg.challenge.poom.global.error.exception.handler;

import gdg.challenge.poom.global.error.code.BaseErrorCode;
import gdg.challenge.poom.global.error.exception.GeneralException;

public class AuthException  extends GeneralException {
    public AuthException(BaseErrorCode code) {
        super(code);
    }
}
