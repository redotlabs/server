package redot.redot_server.domain.eventlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import redot.redot_server.domain.eventlog.entity.EventLogEntity;

@Repository
public interface EventLogRepository extends JpaRepository<EventLogEntity, Long> {
}
