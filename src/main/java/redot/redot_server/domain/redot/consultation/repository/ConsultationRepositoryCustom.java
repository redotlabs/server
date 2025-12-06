package redot.redot_server.domain.redot.consultation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import redot.redot_server.domain.admin.dto.ConsultationSearchCondition;
import redot.redot_server.domain.redot.consultation.entity.Consultation;

public interface ConsultationRepositoryCustom {
    Page<Consultation> findAllBySearchCondition(ConsultationSearchCondition condition, Pageable pageable);
}
