package gdg.challenge.poom.global.error.exception.handler;

import gdg.challenge.poom.global.error.code.BaseErrorCode;
import gdg.challenge.poom.global.error.exception.GeneralException;

public class OAuthException extends GeneralException {
    public OAuthException(BaseErrorCode code) {
        super(code);
    }
}
