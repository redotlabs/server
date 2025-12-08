package redot.redot_server.domain.cms.site.menu.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import redot.redot_server.domain.cms.site.menu.dto.response.CMSMenuResponse;

@Tag(name = "CMS Menu", description = "CMS 메뉴 관리 API")
public interface CMSMenuControllerDocs {

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "CMS 메뉴 목록 조회", description = "선택한 앱의 메뉴 구성을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CMSMenuResponse.class))))
    List<CMSMenuResponse> getMenus(@Parameter(hidden = true) Long redotAppId);
}
