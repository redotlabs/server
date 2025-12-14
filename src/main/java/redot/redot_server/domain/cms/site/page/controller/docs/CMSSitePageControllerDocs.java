package redot.redot_server.domain.cms.site.page.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import redot.redot_server.domain.cms.site.page.dto.request.AppVersionCreateRequest;
import redot.redot_server.domain.cms.site.page.dto.response.AppVersionSummaryResponse;
import redot.redot_server.domain.site.page.dto.response.AppPageResponse;
import redot.redot_server.domain.site.page.entity.AppVersionStatus;
import redot.redot_server.global.util.dto.response.PageResponse;

@Tag(name = "CMS Site Page", description = "CMS 페이지/버전 관리 API")
public interface CMSSitePageControllerDocs {

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "페이지 버전 목록 조회",
            description = "최신순으로 CMS 페이지 버전을 조회하며 status 파라미터로 PUBLISHED/DRAFT만 필터링할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    ResponseEntity<PageResponse<AppVersionSummaryResponse>> getAppVersions(@Parameter(hidden = true) Long redotAppId,
                                                                           @Parameter(description = "필터링할 버전 상태", example = "PUBLISHED") AppVersionStatus status,
                                                                           @ParameterObject Pageable pageable);

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "특정 페이지 조회", description = "앱 내 분리된 페이지 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = AppPageResponse.class)))
    ResponseEntity<AppPageResponse> getPage(@Parameter(hidden = true) Long redotAppId,
                                            @Parameter(name = "pageId", in = ParameterIn.PATH, description = "페이지 ID", example = "1", required = true)
                                            Long pageId);

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "페이지 버전 생성", description = "기존 페이지 유지 + 신규 페이지 추가로 새로운 DRAFT/PUBLISHED 버전을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = AppVersionSummaryResponse.class)))
    ResponseEntity<AppVersionSummaryResponse> createVersion(@Parameter(hidden = true) Long redotAppId,
                                                            @Valid AppVersionCreateRequest request);
}
