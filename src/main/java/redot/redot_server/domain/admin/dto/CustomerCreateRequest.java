package redot.redot_server.domain.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import redot.redot_server.domain.customer.entity.Theme;

public record CustomerCreateRequest(
        @NotBlank String customerName,
        @NotNull String adminEmail,
        Theme theme
) {
    public CustomerCreateRequest{
        if (theme == null) {
            theme = Theme.CLASSIC;
        }
    }
}
