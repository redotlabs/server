package redot.redot_server.domain.redot.consultation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.redot.consultation.dto.request.ConsultationCreateRequest;
import redot.redot_server.domain.redot.consultation.dto.response.ConsultationResponse;
import redot.redot_server.domain.redot.consultation.entity.Consultation;
import redot.redot_server.domain.redot.consultation.repository.ConsultationRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultationService {

    private final ConsultationRepository consultationRepository;

    @Transactional
    public ConsultationResponse createConsultation(ConsultationCreateRequest request) {

        Consultation saved = consultationRepository.save(Consultation.create(request));

        return ConsultationResponse.fromEntity(saved);
    }
}
