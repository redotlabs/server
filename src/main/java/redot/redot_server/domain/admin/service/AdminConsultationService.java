package redot.redot_server.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.dto.ConsultationSearchCondition;
import redot.redot_server.domain.admin.dto.request.ConsultationUpdateRequest;
import redot.redot_server.domain.redot.consultation.dto.response.ConsultationResponse;
import redot.redot_server.domain.redot.consultation.entity.Consultation;
import redot.redot_server.domain.redot.consultation.entity.ConsultationStatus;
import redot.redot_server.domain.redot.consultation.exception.ConsultationErrorCode;
import redot.redot_server.domain.redot.consultation.exception.ConsultationException;
import redot.redot_server.domain.redot.consultation.repository.ConsultationRepository;
import redot.redot_server.global.util.dto.response.PageResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminConsultationService {

    private final ConsultationRepository consultationRepository;

    public ConsultationResponse getConsultationInfo(Long consultationId) {
        Consultation consultation = getConsultation(consultationId);
        return ConsultationResponse.fromEntity(consultation);
    }

    @Transactional
    public ConsultationResponse updateConsultationInfo(Long consultationId, ConsultationUpdateRequest request) {
        Consultation consultation = getConsultation(consultationId);
        consultation.update(request);

        return ConsultationResponse.fromEntity(consultation);
    }

    public PageResponse<ConsultationResponse> getAllConsultationsBySearchCondition(
            ConsultationSearchCondition searchCondition, Pageable pageable) {
        Page<ConsultationResponse> page = consultationRepository
                .findAllBySearchCondition(searchCondition, pageable)
                .map(ConsultationResponse::fromEntity);
        return PageResponse.from(page);
    }

    private Consultation getConsultation(Long consultationId) {
        return consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ConsultationException(ConsultationErrorCode.CONSULTATION_NOT_FOUND));
    }

}
