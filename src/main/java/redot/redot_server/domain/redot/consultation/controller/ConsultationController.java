package redot.redot_server.domain.redot.consultation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.redot.consultation.dto.request.ConsultationCreateRequest;
import redot.redot_server.domain.redot.consultation.dto.response.ConsultationResponse;
import redot.redot_server.domain.redot.consultation.service.ConsultationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/redot/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping
    public ResponseEntity<ConsultationResponse> createConsultation(ConsultationCreateRequest request) {
        ConsultationResponse response = consultationService.createConsultation(request);
        return ResponseEntity.ok(response);
    }
}
