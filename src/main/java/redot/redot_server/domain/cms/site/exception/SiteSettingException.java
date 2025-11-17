package redot.redot_server.domain.cms.site.exception;

import redot.redot_server.support.exception.BaseException;

public class SiteSettingException extends BaseException {
    public SiteSettingException(SiteSettingErrorCode errorCode) {
        super(errorCode);
    }
}
