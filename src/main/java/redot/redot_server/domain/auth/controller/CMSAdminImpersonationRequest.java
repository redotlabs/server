package redot.redot_server.domain.auth.controller;

import jakarta.validation.constraints.NotBlank;

public record CMSAdminImpersonationRequest(
        @NotBlank
        Long customerId
) {
}
