package gdg.challenge.poom.global.error.exception.handler;

import gdg.challenge.poom.global.error.code.BaseErrorCode;
import gdg.challenge.poom.global.error.exception.GeneralException;

public class ChatException extends GeneralException {
    public ChatException(BaseErrorCode code) {
        super(code);
    }
}
