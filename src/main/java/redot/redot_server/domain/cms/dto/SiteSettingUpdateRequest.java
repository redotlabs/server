package redot.redot_server.domain.cms.dto;

import jakarta.validation.constraints.NotNull;

public record SiteSettingUpdateRequest(
        @NotNull
        String logoUrl,
        @NotNull
        String siteName,
        @NotNull
        String customDomain,
        @NotNull
        String gaInfo
) {
}
