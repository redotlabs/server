package redot.redot_server.domain.redot.app.dto.response;

import java.time.LocalDateTime;
import redot.redot_server.domain.redot.app.entity.RedotApp;
import redot.redot_server.domain.redot.app.entity.RedotAppStatus;

public record RedotAppResponse(
        Long id,
        String name,
        RedotAppStatus status,
        boolean isCreatedManager,
        LocalDateTime createdAt,
        Long planId
) {
    public static RedotAppResponse fromEntity(RedotApp redotApp) {
        return new RedotAppResponse(
                redotApp.getId(),
                redotApp.getName(),
                redotApp.getStatus(),
                redotApp.isCreatedManager(),
                redotApp.getCreatedAt(),
                redotApp.getPlan().getId()
        );
    }
}
