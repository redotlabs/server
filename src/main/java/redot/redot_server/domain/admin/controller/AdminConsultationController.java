package redot.redot_server.domain.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.admin.controller.docs.AdminConsultationControllerDocs;
import redot.redot_server.domain.admin.dto.ConsultationSearchCondition;
import redot.redot_server.domain.admin.dto.request.ConsultationUpdateRequest;
import redot.redot_server.domain.admin.service.AdminConsultationService;
import redot.redot_server.domain.redot.consultation.dto.response.ConsultationResponse;
import redot.redot_server.global.util.dto.response.PageResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/redot/admin/consultations")
public class AdminConsultationController implements AdminConsultationControllerDocs {

    private final AdminConsultationService adminConsultationService;

    @GetMapping("/{consultationId}")
    @Override
    public ResponseEntity<ConsultationResponse> getConsultationInfo(
            @PathVariable("consultationId") Long consultationId) {
        return ResponseEntity.ok(adminConsultationService.getConsultationInfo(consultationId));
    }

    @GetMapping
    @Override
    public ResponseEntity<PageResponse<ConsultationResponse>> getAllConsultations(
            ConsultationSearchCondition searchCondition,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        ConsultationSearchCondition condition = searchCondition == null
                ? ConsultationSearchCondition.empty()
                : searchCondition;
        PageResponse<ConsultationResponse> response = adminConsultationService
                .getAllConsultationsBySearchCondition(condition, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{consultationId}")
    @Override
    public ResponseEntity<ConsultationResponse> updateConsultationInfo(
            @RequestBody @Valid ConsultationUpdateRequest request,
            @PathVariable("consultationId") Long consultationId) {
        return ResponseEntity.ok(adminConsultationService.updateConsultationInfo(consultationId, request));
    }
}
