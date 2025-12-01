package redot.redot_server.domain.cms.menu.dto;

import java.time.LocalDateTime;
import redot.redot_server.domain.cms.menu.entity.CmsMenu;

public record CmsMenuResponse(
        Long id,
        Long planId,
        String name,
        String path,
        Integer order,
        LocalDateTime createdAt

) {
    public static CmsMenuResponse fromEntity(CmsMenu cmsMenu) {
        return new CmsMenuResponse(
                cmsMenu.getId(),
                cmsMenu.getPlan().getId(),
                cmsMenu.getName(),
                cmsMenu.getPath(),
                cmsMenu.getOrder(),
                cmsMenu.getCreatedAt()
        );
    }
}

