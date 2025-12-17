package redot.redot_server.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AdminDashboardStatsResponse(
        @Schema(description = "전체 Redot 회원 수") long totalRedotMembers,
        @Schema(description = "전일까지 누적된 Redot 회원 수") long redotMembersUntilYesterday,
        @Schema(description = "전일 이후 가입한 Redot 회원 수") long newRedotMembersSinceYesterday,
        @Schema(description = "누적 상담 요청 수") long consultationRequestCount,
        @Schema(description = "등록된 관리자 수") long adminCount
) {
}
