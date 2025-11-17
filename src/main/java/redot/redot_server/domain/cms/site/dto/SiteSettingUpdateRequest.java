package redot.redot_server.domain.cms.site.dto;

public record SiteSettingUpdateRequest(
        String logoUrl,
        String siteName,
        String customDomain,
        String gaInfo
) {
}
