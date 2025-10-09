package redot.redot_server.domain.cms.exception;

import redot.redot_server.global.exception.BaseException;

public class CustomerException extends BaseException {
    public CustomerException(CustomerErrorCode errorCode) {
        super(errorCode);
    }
}
