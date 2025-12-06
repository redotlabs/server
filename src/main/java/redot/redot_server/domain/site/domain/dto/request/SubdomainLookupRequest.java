package redot.redot_server.domain.site.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SubdomainLookupRequest(
        @NotBlank(message = "커스텀 도메인을 입력해주세요.")
        String customDomain
) {
}
