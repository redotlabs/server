package redot.redot_server.domain.cms.dto;

import jakarta.validation.constraints.NotNull;
import redot.redot_server.domain.cms.entity.CMSMemberRole;

public record CMSMemberRoleRequest(
        @NotNull
        CMSMemberRole role
) {
}
