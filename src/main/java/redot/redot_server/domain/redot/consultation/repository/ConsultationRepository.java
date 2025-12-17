package redot.redot_server.domain.redot.consultation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.redot.consultation.entity.Consultation;
import redot.redot_server.domain.redot.consultation.entity.ConsultationStatus;

public interface ConsultationRepository extends JpaRepository<Consultation, Long>, ConsultationRepositoryCustom {

    long countByStatus(ConsultationStatus status);
}
