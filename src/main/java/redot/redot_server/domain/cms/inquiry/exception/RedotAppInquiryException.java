package redot.redot_server.domain.cms.inquiry.exception;

import redot.redot_server.global.exception.BaseException;

public class RedotAppInquiryException extends BaseException {
    public RedotAppInquiryException(RedotAppInquiryErrorCode errorCode) {
        super(errorCode);
    }
}
