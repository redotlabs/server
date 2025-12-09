package redot.redot_server.domain.cms.site.page.service.dto;

import redot.redot_server.domain.cms.site.page.dto.response.AppVersionPageSummaryResponse;

public record AppVersionPageSummaryWithVersionResponse(
        Long appVersionId,
        Long id,
        String path,
        String title,
        String description
) {

    public AppVersionPageSummaryResponse toSummary() {
        return new AppVersionPageSummaryResponse(id, path, title, description);
    }
}
