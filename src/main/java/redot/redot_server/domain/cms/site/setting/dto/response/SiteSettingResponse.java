package redot.redot_server.domain.cms.site.setting.dto.response;

import redot.redot_server.domain.site.domain.entity.Domain;
import redot.redot_server.domain.site.setting.entity.SiteSetting;
import redot.redot_server.global.s3.util.ImageUrlResolver;

public record SiteSettingResponse(
        String logoUrl,
        String siteName,
        String subdomain,
        String customDomain,
        String gaInfo
) {
    public static SiteSettingResponse fromEntity(SiteSetting siteSetting, Domain domain,
                                                 ImageUrlResolver imageUrlResolver) {
        return new SiteSettingResponse(
                imageUrlResolver.toPublicUrl(siteSetting.getLogoUrl()),
                siteSetting.getSiteName(),
                domain.getSubdomain(),
                domain.getCustomDomain(),
                siteSetting.getGaInfo()
        );
    }
}
