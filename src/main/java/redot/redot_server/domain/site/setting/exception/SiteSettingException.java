package redot.redot_server.domain.site.setting.exception;

import redot.redot_server.global.exception.BaseException;

public class SiteSettingException extends BaseException {
    public SiteSettingException(SiteSettingErrorCode errorCode) {
        super(errorCode);
    }
}
