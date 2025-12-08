package redot.redot_server.domain.cms.site.page.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import redot.redot_server.domain.site.page.entity.AppVersion;
import redot.redot_server.domain.site.page.entity.AppVersionStatus;

public record AppVersionSummaryResponse(
        Long id,
        AppVersionStatus status,
        LocalDateTime createdAt,
        String remark,
        List<AppVersionPageSummaryResponse> pages
) {

    public static AppVersionSummaryResponse from(AppVersion version,
                                                 List<AppVersionPageSummaryResponse> pages) {
        return new AppVersionSummaryResponse(
                version.getId(),
                version.getStatus(),
                version.getCreatedAt(),
                version.getRemark(),
                pages
        );
    }
}
