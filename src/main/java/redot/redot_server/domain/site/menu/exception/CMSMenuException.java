package redot.redot_server.domain.site.menu.exception;

import lombok.Getter;
import redot.redot_server.global.exception.BaseException;

@Getter
public class CMSMenuException extends BaseException {
    private final CMSMenuErrorCode errorCode;

    public CMSMenuException(CMSMenuErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}

