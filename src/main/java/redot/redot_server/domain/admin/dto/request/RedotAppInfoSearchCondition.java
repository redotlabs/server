package redot.redot_server.domain.admin.dto.request;

import redot.redot_server.domain.redot.app.entity.RedotAppStatus;

public record RedotAppInfoSearchCondition(
        String name,
        Long redotMemberId,
        RedotAppStatus status
) {
}
