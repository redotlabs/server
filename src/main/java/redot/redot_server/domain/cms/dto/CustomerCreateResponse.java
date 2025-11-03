package redot.redot_server.domain.cms.dto;

public record CustomerCreateResponse(
        CustomerResponse customer,
        SiteSettingResponse siteSetting,
        StyleInfoResponse styleInfo,
        CMSMemberResponse owner
) {
}
