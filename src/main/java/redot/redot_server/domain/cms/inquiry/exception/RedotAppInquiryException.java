package redot.redot_server.domain.cms.inquiry.exception;

import redot.redot_server.support.exception.BaseException;

public class RedotAppInquiryException extends BaseException {
    public RedotAppInquiryException(RedotAppInquiryErrorCode errorCode) {
        super(errorCode);
    }
}
