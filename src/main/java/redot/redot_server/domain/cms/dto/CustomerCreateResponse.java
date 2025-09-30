package redot.redot_server.domain.cms.dto;

import redot.redot_server.domain.cms.entity.Theme;

public record CustomerCreateResponse(
        Long id,
        String name,
        Theme theme,
        String domainName,
        CMSMemberDTO owner
) {
}
