package gdg.challenge.poom.global.error.exception.handler;

import gdg.challenge.poom.global.error.code.BaseErrorCode;
import gdg.challenge.poom.global.error.exception.GeneralException;

public class JournalException extends GeneralException {
    public JournalException(BaseErrorCode code) {
        super(code);
    }
}
