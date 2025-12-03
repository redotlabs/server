package redot.redot_server.domain.cms.style.exception;

import redot.redot_server.global.exception.BaseException;

public class StyleInfoException extends BaseException{
    public StyleInfoException(StyleInfoErrorCode errorCode) {
        super(errorCode);
    }
}
