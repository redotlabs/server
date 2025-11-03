package redot.redot_server.domain.cms.dto;

import redot.redot_server.domain.cms.entity.StyleInfo;
import redot.redot_server.domain.cms.entity.Theme;

public record StyleInfoResponse(
        String color,
        String font,
        Theme theme
) {
    public static StyleInfoResponse fromEntity(StyleInfo styleInfo) {
        return new StyleInfoResponse(
                styleInfo.getColor(),
                styleInfo.getFont(),
                styleInfo.getTheme()
        );
    }
}
