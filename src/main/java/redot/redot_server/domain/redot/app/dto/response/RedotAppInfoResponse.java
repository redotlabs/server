package redot.redot_server.domain.redot.app.dto.response;

import redot.redot_server.domain.redot.member.dto.response.RedotMemberResponse;
import redot.redot_server.domain.cms.site.setting.dto.response.SiteSettingResponse;
import redot.redot_server.domain.cms.site.style.dto.response.StyleInfoResponse;

public record RedotAppInfoResponse(
        RedotAppResponse redotApp,
        SiteSettingResponse siteSetting,
        StyleInfoResponse styleInfo,
        RedotMemberResponse owner
) {
}
