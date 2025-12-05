package redot.redot_server.domain.redot.app.exception;

import redot.redot_server.global.exception.BaseException;

public class RedotAppException extends BaseException {
    public RedotAppException(RedotAppErrorCode errorCode) {
        super(errorCode);
    }
}
