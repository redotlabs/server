package redot.redot_server.domain.cms.site.setting.dto.request;

public record SiteSettingUpdateRequest(
        String logoUrl,
        String siteName,
        String customDomain,
        String gaInfo
) {
}
