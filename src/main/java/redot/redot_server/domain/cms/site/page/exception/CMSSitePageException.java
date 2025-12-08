package redot.redot_server.domain.cms.site.page.exception;

import redot.redot_server.global.exception.BaseException;

public class CMSSitePageException extends BaseException {
    public CMSSitePageException(CMSSitePageErrorCode errorCode) {
        super(errorCode);
    }
}
