package redot.redot_server.domain.auth.dto;

import jakarta.validation.constraints.NotNull;

public record CMSAdminImpersonationRequest(
        @NotNull(message = "고객 ID를 입력해주세요.")
        Long customerId
) {
}
