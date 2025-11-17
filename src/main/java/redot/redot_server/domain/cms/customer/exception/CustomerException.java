package redot.redot_server.domain.cms.customer.exception;

import redot.redot_server.support.exception.BaseException;

public class CustomerException extends BaseException {
    public CustomerException(CustomerErrorCode errorCode) {
        super(errorCode);
    }
}
