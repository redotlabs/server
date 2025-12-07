package redot.redot_server.domain.redot.plan.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import redot.redot_server.domain.redot.plan.dto.response.PlanResponse;

@Tag(name = "Plan", description = "플랜 관리 API")
public interface PlanControllerDocs {

    @Operation(summary = "플랜 목록 조회", description = "이용 가능한 모든 플랜 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PlanResponse.class))))
    List<PlanResponse> getAllPlans();
}
