package redot.redot_server.domain.redot.consultation.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import redot.redot_server.domain.redot.consultation.dto.request.ConsultationCreateRequest;
import redot.redot_server.domain.redot.consultation.dto.response.ConsultationResponse;

@Tag(name = "Consultation", description = "상담 신청 API")
public interface ConsultationControllerDocs {

    @Operation(summary = "상담 신청 생성", description = "서비스 상담 신청을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = ConsultationResponse.class)))
    ResponseEntity<ConsultationResponse> createConsultation(ConsultationCreateRequest request);
}
