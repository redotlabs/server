package redot.redot_server.domain.cms.redotapp.dto;

import redot.redot_server.domain.redot.member.dto.RedotMemberResponse;
import redot.redot_server.domain.cms.site.dto.SiteSettingResponse;
import redot.redot_server.domain.cms.style.dto.StyleInfoResponse;

public record RedotAppInfoResponse(
        RedotAppResponse redotApp,
        SiteSettingResponse siteSetting,
        StyleInfoResponse styleInfo,
        RedotMemberResponse owner
) {
}
