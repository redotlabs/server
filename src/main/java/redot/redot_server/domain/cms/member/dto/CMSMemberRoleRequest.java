package redot.redot_server.domain.cms.member.dto;

import jakarta.validation.constraints.NotNull;
import redot.redot_server.domain.cms.member.entity.CMSMemberRole;

public record CMSMemberRoleRequest(
        @NotNull
        CMSMemberRole role
) {
}
