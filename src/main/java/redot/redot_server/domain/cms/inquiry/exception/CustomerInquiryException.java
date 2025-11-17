package redot.redot_server.domain.cms.inquiry.exception;

import redot.redot_server.support.exception.BaseException;

public class CustomerInquiryException extends BaseException {
    public CustomerInquiryException(CustomerInquiryErrorCode errorCode) {
        super(errorCode);
    }
}
