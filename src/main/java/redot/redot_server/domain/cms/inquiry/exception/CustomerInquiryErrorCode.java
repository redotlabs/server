package redot.redot_server.domain.cms.inquiry.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redot.redot_server.support.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum CustomerInquiryErrorCode implements ErrorCode {
    CUSTOMER_INQUIRY_NOT_FOUND(404, 7000, "고객 문의를 찾을 수 없습니다."),
    CUSTOMER_INQUIRY_ALREADY_PROCESSED(400, 7001, "이미 처리된 고객 문의입니다."),
    CUSTOMER_INQUIRY_NOT_COMPLETED(400, 7002, "처리되지 않은 고객 문의입니다."),
    INQUIRY_NUMBER_EXHAUSTED(500, 7003, "문의 번호가 소진되었습니다.");


    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}
