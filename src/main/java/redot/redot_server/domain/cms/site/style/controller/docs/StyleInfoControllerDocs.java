package redot.redot_server.domain.cms.site.style.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import redot.redot_server.domain.cms.site.style.dto.request.StyleInfoUpdateRequest;
import redot.redot_server.domain.cms.site.style.dto.response.StyleInfoResponse;

@Tag(name = "Style Info", description = "스타일 정보 API")
public interface StyleInfoControllerDocs {

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "스타일 정보 조회", description = "CMS 앱의 스타일 테마 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = StyleInfoResponse.class)))
    ResponseEntity<StyleInfoResponse> getStyleInfo(@Parameter(hidden = true) Long redotAppId);

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "스타일 정보 수정", description = "스타일 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(schema = @Schema(implementation = StyleInfoResponse.class)))
    ResponseEntity<StyleInfoResponse> updateStyleInfo(@Parameter(hidden = true) Long redotAppId,
                                                      @Valid StyleInfoUpdateRequest request);
}
