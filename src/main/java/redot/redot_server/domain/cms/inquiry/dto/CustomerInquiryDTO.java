package redot.redot_server.domain.cms.inquiry.dto;

import java.time.LocalDateTime;
import redot.redot_server.domain.cms.inquiry.entity.CustomerInquiryStatus;

public record CustomerInquiryDTO(
        Long id,
        Long customerId,
        String inquiryNumber,
        String inquirerName,
        String title,
        String content,
        CustomerInquiryStatus status,
        LocalDateTime createdAt
) {
}
