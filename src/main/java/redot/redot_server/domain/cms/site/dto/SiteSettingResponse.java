package redot.redot_server.domain.cms.site.dto;

import redot.redot_server.domain.admin.entity.Domain;
import redot.redot_server.domain.cms.site.entity.SiteSetting;

public record SiteSettingResponse(
        String logoUrl,
        String siteName,
        String subdomain,
        String customDomain,
        String gaInfo
) {
    public static SiteSettingResponse fromEntity(SiteSetting siteSetting, Domain domain) {
        return new SiteSettingResponse(
                siteSetting.getLogoUrl(),
                siteSetting.getSiteName(),
                domain.getSubdomain(),
                domain.getCustomDomain(),
                siteSetting.getGaInfo()
        );
    }
}
