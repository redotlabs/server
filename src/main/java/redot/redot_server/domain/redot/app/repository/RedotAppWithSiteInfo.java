package redot.redot_server.domain.redot.app.repository;

import redot.redot_server.domain.redot.app.entity.RedotApp;
import redot.redot_server.domain.site.domain.entity.Domain;
import redot.redot_server.domain.site.setting.entity.SiteSetting;
import redot.redot_server.domain.site.style.entity.StyleInfo;

public record RedotAppWithSiteInfo(
        RedotApp redotApp,
        Domain domain,
        SiteSetting siteSetting,
        StyleInfo styleInfo
) {
}
