package redot.redot_server.domain.cms.site.page.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AppPageCreateRequest(
        @NotBlank String content,
        @NotBlank String path,
        String description,
        @NotNull Boolean isProtected,
        @NotBlank String title
) {
}
