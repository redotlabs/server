package redot.redot_server.domain.admin.dto.request;

import jakarta.validation.constraints.NotNull;
import redot.redot_server.domain.redot.app.entity.RedotAppStatus;

public record RedotAppStatusUpdateRequest(
        @NotNull(message = "status 는 필수입니다.") RedotAppStatus status,
        String remark
) {
}
