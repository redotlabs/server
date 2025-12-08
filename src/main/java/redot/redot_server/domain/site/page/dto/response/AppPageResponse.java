package redot.redot_server.domain.site.page.dto.response;

import java.time.LocalDateTime;
import redot.redot_server.domain.site.page.entity.AppPage;

public record AppPageResponse(
        Long id,
        String content,
        String path,
        String description,
        boolean isProtected,
        String title,
        LocalDateTime createdAt
) {
    public static AppPageResponse from(AppPage page) {
        return new AppPageResponse(
                page.getId(),
                page.getContent(),
                page.getPath(),
                page.getDescription(),
                page.getIsProtected(),
                page.getTitle(),
                page.getCreatedAt()
        );
    }
}
