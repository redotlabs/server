package redot.redot_server.domain.site.page.exception;

import redot.redot_server.global.exception.BaseException;

public class SitePageException extends BaseException {
    public SitePageException(SitePageErrorCode errorCode) {
        super(errorCode);
    }
}
