package redot.redot_server.domain.cms.site.page.dto.request;

import jakarta.validation.constraints.NotNull;

public record AppPageRetainRequest(
        @NotNull Long id
) {
}
