package redot.redot_server.domain.cms.customer.dto;

import redot.redot_server.domain.cms.member.dto.CMSMemberResponse;
import redot.redot_server.domain.cms.site.dto.SiteSettingResponse;
import redot.redot_server.domain.cms.style.dto.StyleInfoResponse;

public record CustomerInfoResponse(
        CustomerResponse customer,
        SiteSettingResponse siteSetting,
        StyleInfoResponse styleInfo,
        CMSMemberResponse owner
) {
}
