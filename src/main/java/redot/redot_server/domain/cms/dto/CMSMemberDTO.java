package redot.redot_server.domain.cms.dto;

import redot.redot_server.domain.cms.entity.CMSMemberRole;

public record CMSMemberDTO(
        Long customerId,
        Long id,
        String name,
        String email,
        CMSMemberRole role
) {
}
