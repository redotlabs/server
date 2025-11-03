package redot.redot_server.domain.cms.exception;

import redot.redot_server.global.exception.BaseException;

public class SiteSettingException extends BaseException {
    public SiteSettingException(SiteSettingErrorCode errorCode) {
        super(errorCode);
    }
}
