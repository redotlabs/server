package redot.redot_server.domain.cms.redotapp.dto;

import java.time.LocalDateTime;
import redot.redot_server.domain.cms.redotapp.entity.RedotApp;
import redot.redot_server.domain.cms.redotapp.entity.RedotAppStatus;

public record RedotAppResponse(
        Long id,
        String appName,
        RedotAppStatus status,
        boolean isCreatedManager,
        LocalDateTime createdAt
) {
    public static RedotAppResponse fromEntity(RedotApp redotApp) {
        return new RedotAppResponse(
                redotApp.getId(),
                redotApp.getAppName(),
                redotApp.getStatus(),
                redotApp.isCreatedManager(),
                redotApp.getCreatedAt()
        );
    }
}
