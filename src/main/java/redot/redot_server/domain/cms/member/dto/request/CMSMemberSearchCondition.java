package redot.redot_server.domain.cms.member.dto.request;

import redot.redot_server.domain.cms.member.entity.CMSMemberRole;

public record CMSMemberSearchCondition(
    String name,
    String email,
    CMSMemberRole role
) {
}
