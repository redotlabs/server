package redot.redot_server.domain.cms.menu.exception;

import lombok.Getter;
import redot.redot_server.support.exception.BaseException;

@Getter
public class CmsMenuException extends BaseException {
    private final CmsMenuErrorCode errorCode;

    public CmsMenuException(CmsMenuErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}

