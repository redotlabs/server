package redot.redot_server.domain.redot.app.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springdoc.core.annotations.ParameterObject;
import redot.redot_server.domain.redot.app.dto.request.RedotAppCreateManagerRequest;
import redot.redot_server.domain.redot.app.dto.request.RedotAppCreateRequest;
import redot.redot_server.domain.redot.app.dto.response.RedotAppInfoResponse;
import redot.redot_server.global.security.principal.JwtPrincipal;
import redot.redot_server.global.util.dto.response.PageResponse;

@Tag(name = "Redot App", description = "Redot 앱 API")
public interface RedotAppControllerDocs {

    @Operation(summary = "앱 기본 정보 조회", description = "현재 도메인에 매핑된 Redot 앱 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = RedotAppInfoResponse.class)))
    ResponseEntity<RedotAppInfoResponse> getRedotAppInfo(@Parameter(hidden = true) Long redotAppId);

    @Operation(summary = "앱 목록 조회", description = "`page`, `size`, `sort=createdAt,desc` 파라미터와 함께 요청하면 자신이 속한 앱 목록을 최신순으로 확인할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    ResponseEntity<PageResponse<RedotAppInfoResponse>> getRedotAppList(@Parameter(hidden = true) JwtPrincipal jwtPrincipal,
                                                                       @Parameter(description = "기본 정렬 createdAt DESC")
                                                                       @ParameterObject Pageable pageable);

    @Operation(summary = "앱 생성", description = "새로운 Redot 앱을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = RedotAppInfoResponse.class)))
    ResponseEntity<RedotAppInfoResponse> createRedotApp(RedotAppCreateRequest request,
                                                        @Parameter(hidden = true) JwtPrincipal jwtPrincipal);

    @Operation(summary = "앱 매니저 생성", description = "Redot 앱에 추가 매니저를 초대합니다.")
    @ApiResponse(responseCode = "200", description = "생성 성공")
    ResponseEntity<Void> createManager(@Parameter(description = "Redot 앱 ID", example = "1") Long redotAppId,
                                       RedotAppCreateManagerRequest request,
                                       @Parameter(hidden = true) JwtPrincipal jwtPrincipal);
}
