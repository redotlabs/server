package redot.redot_server.domain.cms.inquiry.dto.response;

import java.time.LocalDateTime;
import redot.redot_server.domain.cms.inquiry.entity.RedotAppInquiryStatus;

public record RedotAppInquiryResponse(
        Long id,
        Long redotAppId,
        String inquiryNumber,
        String inquirerName,
        String title,
        String content,
        RedotAppInquiryStatus status,
        LocalDateTime createdAt
) {
}
