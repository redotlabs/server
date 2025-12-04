package redot.redot_server.domain.cms.inquiry.dto.request;

import java.time.LocalDate;
import redot.redot_server.domain.cms.inquiry.entity.RedotAppInquiryStatus;

public record RedotAppInquirySearchCondition(
        RedotAppInquiryStatus status,
        String inquiryNumber,
        String title,
        String inquirerName,
        LocalDate startDate,
        LocalDate endDate
) {
}
