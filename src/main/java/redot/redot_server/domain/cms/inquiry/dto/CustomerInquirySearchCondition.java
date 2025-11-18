package redot.redot_server.domain.cms.inquiry.dto;

import java.time.LocalDate;
import redot.redot_server.domain.cms.inquiry.entity.CustomerInquiryStatus;

public record CustomerInquirySearchCondition(
        CustomerInquiryStatus status,
        String inquiryNumber,
        String title,
        String inquirerName,
        LocalDate startDate,
        LocalDate endDate
) {
}
