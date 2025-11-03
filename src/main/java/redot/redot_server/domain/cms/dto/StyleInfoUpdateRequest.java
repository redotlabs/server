package redot.redot_server.domain.cms.dto;

import redot.redot_server.domain.cms.entity.Theme;

public record StyleInfoUpdateRequest(
        String color,
        String font,
        Theme theme
) {
}
