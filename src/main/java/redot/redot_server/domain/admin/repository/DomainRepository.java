package redot.redot_server.domain.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.admin.entity.Domain;

public interface DomainRepository extends JpaRepository<Domain, Long> {

}
