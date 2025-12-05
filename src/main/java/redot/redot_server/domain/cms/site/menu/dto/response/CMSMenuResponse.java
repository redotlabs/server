package redot.redot_server.domain.cms.site.menu.dto.response;

import java.time.LocalDateTime;
import redot.redot_server.domain.site.menu.entity.CMSMenu;

public record CMSMenuResponse(
        Long id,
        Long planId,
        String name,
        String path,
        Integer order,
        LocalDateTime createdAt

) {
    public static CMSMenuResponse fromEntity(CMSMenu cmsMenu) {
        return new CMSMenuResponse(
                cmsMenu.getId(),
                cmsMenu.getPlan().getId(),
                cmsMenu.getName(),
                cmsMenu.getPath(),
                cmsMenu.getOrder(),
                cmsMenu.getCreatedAt()
        );
    }
}

