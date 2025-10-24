package redot.redot_server.domain.cms.dto;

import redot.redot_server.domain.cms.entity.SiteSetting;

public record SiteSettingResponse(
        String logoUrl,
        String siteName,
        String customDomain,
        String gaInfo
) {
    public static SiteSettingResponse fromEntity(SiteSetting siteSetting, String customDomain) {
        return new SiteSettingResponse(
                siteSetting.getLogoUrl(),
                siteSetting.getSiteName(),
                customDomain,
                siteSetting.getGaInfo()
        );
    }
}
