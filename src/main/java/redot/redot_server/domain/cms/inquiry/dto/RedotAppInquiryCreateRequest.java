package redot.redot_server.domain.cms.inquiry.dto;

import jakarta.validation.constraints.NotBlank;

public record RedotAppInquiryCreateRequest(
        @NotBlank(message = "문의자 이름은 필수값입니다.")
        String inquirerName,
        @NotBlank(message = "문의 제목은 필수값입니다.")
        String title,
        @NotBlank(message = "문의 내용은 필수값입니다.")
        String content
) {
}
