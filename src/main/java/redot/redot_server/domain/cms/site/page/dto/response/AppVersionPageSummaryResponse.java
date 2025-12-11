package redot.redot_server.domain.cms.site.page.dto.response;

public record AppVersionPageSummaryResponse(
        Long id,
        String path,
        String title,
        String description,
        boolean isProtected
) {
}
