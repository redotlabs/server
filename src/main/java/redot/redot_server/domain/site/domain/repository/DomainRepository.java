package redot.redot_server.domain.site.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.site.domain.entity.Domain;

public interface DomainRepository extends JpaRepository<Domain, Long> {

    @EntityGraph(attributePaths = "redotApp")
    Optional<Domain> findBySubdomain(String subdomain);

    @EntityGraph(attributePaths = "redotApp")
    Optional<Domain> findByCustomDomain(String customDomain);

    Optional<Domain> findByRedotAppId(Long redotAppId);

    boolean existsByCustomDomain(String customDomain);
}
