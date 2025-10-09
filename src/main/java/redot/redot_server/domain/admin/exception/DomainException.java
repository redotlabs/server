package redot.redot_server.domain.admin.exception;

import redot.redot_server.global.exception.BaseException;

public class DomainException extends BaseException {
    public DomainException(DomainErrorCode errorCode) {
        super(errorCode);
    }
}
