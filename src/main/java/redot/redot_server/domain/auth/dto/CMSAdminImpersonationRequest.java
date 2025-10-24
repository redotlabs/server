package redot.redot_server.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record CMSAdminImpersonationRequest(
        @NotBlank
        Long customerId
) {
}
