package redot.redot_server.domain.cms.redotapp.dto.response;

import redot.redot_server.domain.redot.member.dto.response.RedotMemberResponse;
import redot.redot_server.domain.cms.site.dto.response.SiteSettingResponse;
import redot.redot_server.domain.cms.style.dto.response.StyleInfoResponse;

public record RedotAppInfoResponse(
        RedotAppResponse redotApp,
        SiteSettingResponse siteSetting,
        StyleInfoResponse styleInfo,
        RedotMemberResponse owner
) {
}
