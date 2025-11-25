package redot.redot_server.domain.cms.redotapp.exception;

import redot.redot_server.support.exception.BaseException;

public class RedotAppException extends BaseException {
    public RedotAppException(RedotAppErrorCode errorCode) {
        super(errorCode);
    }
}
