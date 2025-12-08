package redot.redot_server.domain.site.page.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import redot.redot_server.domain.site.page.dto.response.AppPageResponse;

@Tag(name = "Site Page", description = "사이트 공개 페이지 API")
public interface SitePageControllerDocs {

    @Operation(summary = "사이트 페이지 조회", description = "배포된 버전에서 path에 해당하는 페이지를 제공합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = AppPageResponse.class)))
    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 사이트 앱의 서브도메인")
    ResponseEntity<AppPageResponse> getSitePages(@Parameter(hidden = true) Long appId,
                                                 @Parameter(name = "path", description = "페이지 경로", example = "/") String path);
}
