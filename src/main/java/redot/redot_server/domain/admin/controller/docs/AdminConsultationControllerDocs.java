package redot.redot_server.domain.admin.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springdoc.core.annotations.ParameterObject;
import redot.redot_server.domain.admin.dto.ConsultationSearchCondition;
import redot.redot_server.domain.admin.dto.request.ConsultationUpdateRequest;
import redot.redot_server.domain.redot.consultation.dto.response.ConsultationResponse;
import redot.redot_server.global.util.dto.response.PageResponse;

@Tag(name = "Admin Consultation", description = "관리자 상담 관리 API")
public interface AdminConsultationControllerDocs {

    @Operation(summary = "상담 단건 조회", description = "상담 ID로 상담 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ConsultationResponse.class)))
    ResponseEntity<ConsultationResponse> getConsultationInfo(@Parameter(description = "상담 ID", example = "1") Long consultationId);

    @Operation(summary = "상담 목록 조회", description = "`email`, `phone`, `status`, `type`, `currentWebsiteUrl`, `startDate`, `endDate` 검색 조건을 조합해 조회하며 `sort=createdAt,desc` 와 같은 Pageable 쿼리 파라미터를 사용합니다. `status` 파라미터를 생략하면 CANCELLED 상태 상담은 응답에서 제외됩니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    ResponseEntity<PageResponse<ConsultationResponse>> getAllConsultations(
            @ParameterObject ConsultationSearchCondition searchCondition,
            @Parameter(description = "기본 정렬은 createdAt DESC 입니다.") @ParameterObject Pageable pageable);

    @Operation(summary = "상담 정보 수정", description = "상담 상태나 담당자 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(schema = @Schema(implementation = ConsultationResponse.class)))
    ResponseEntity<ConsultationResponse> updateConsultationInfo(ConsultationUpdateRequest request,
                                                                @Parameter(description = "상담 ID", example = "1") Long consultationId);
}
