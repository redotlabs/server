package redot.redot_server.domain.cms.style.dto;

import redot.redot_server.domain.cms.site.entity.Theme;

public record StyleInfoUpdateRequest(
        String color,
        String font,
        Theme theme
) {
}
