package redot.redot_server.domain.admin.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import redot.redot_server.domain.admin.dto.request.RedotAppInfoSearchCondition;
import redot.redot_server.domain.redot.app.dto.request.RedotAppCreateRequest;
import redot.redot_server.domain.redot.app.dto.response.RedotAppInfoResponse;
import redot.redot_server.global.util.dto.response.PageResponse;

@Tag(name = "Admin Redot App", description = "관리자 앱 관리 API")
public interface AdminRedotAppControllerDocs {

    @Operation(summary = "Redot 앱 생성", description = "관리자 권한으로 신규 Redot 앱을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = RedotAppInfoResponse.class)))
    ResponseEntity<RedotAppInfoResponse> createRedotApp(@Valid RedotAppCreateRequest request);

    @Operation(summary = "앱 목록 조회", description = "status, name, redot_member_id 파라미터로 필터링하며 createdAt 기준 최신순 정렬을 기본으로 제공합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    ResponseEntity<PageResponse<RedotAppInfoResponse>> getRedotAppInfoList(
            @ParameterObject RedotAppInfoSearchCondition searchCondition,
            @ParameterObject Pageable pageable);
}
