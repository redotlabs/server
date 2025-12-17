package redot.redot_server.domain.admin.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import redot.redot_server.domain.admin.dto.response.AdminDashboardStatsResponse;

@Tag(name = "Admin Dashboard", description = "관리자 대시보드 통계 API")
public interface AdminDashboardControllerDocs {

    @Operation(summary = "대시보드 통계 조회", description = "관리자 대시보드에 필요한 요약 통계를 제공합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = AdminDashboardStatsResponse.class)))
    ResponseEntity<AdminDashboardStatsResponse> getDashboardStats();
}
