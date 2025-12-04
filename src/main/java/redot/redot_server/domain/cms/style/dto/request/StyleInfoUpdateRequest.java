package redot.redot_server.domain.cms.style.dto.request;

import redot.redot_server.domain.cms.site.entity.Theme;

public record StyleInfoUpdateRequest(
        String color,
        String font,
        Theme theme
) {
}
