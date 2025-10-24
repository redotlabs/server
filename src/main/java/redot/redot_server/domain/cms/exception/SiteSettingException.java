package redot.redot_server.domain.cms.exception;

import redot.redot_server.global.exception.BaseException;
import redot.redot_server.global.exception.ErrorCode;

public class SiteSettingException extends BaseException {
    public SiteSettingException(ErrorCode errorCode) {
        super(errorCode);
    }
}
