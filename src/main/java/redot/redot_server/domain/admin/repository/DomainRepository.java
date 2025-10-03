package redot.redot_server.domain.admin.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.admin.entity.Domain;

public interface DomainRepository extends JpaRepository<Domain, Long> {

    Optional<Domain> findByDomainName(String domainName);

    Optional<Domain> findByCustomDomain(String customDomain);
}
