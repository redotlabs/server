package redot.redot_server.domain.cms.dto;

public record CustomerInfoResponse(
        CustomerResponse customer,
        SiteSettingResponse siteSetting,
        StyleInfoResponse styleInfo,
        CMSMemberResponse owner
) {
}
