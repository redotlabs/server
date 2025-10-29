package redot.redot_server.domain.cms.exception;

import redot.redot_server.global.exception.BaseException;

public class CustomerInquiryException extends BaseException {
    public CustomerInquiryException(CustomerInquiryErrorCode errorCode) {
        super(errorCode);
    }
}
