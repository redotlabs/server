package redot.redot_server.domain.cms.redotapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.cms.redotapp.entity.RedotApp;

public interface RedotAppRepository extends JpaRepository<RedotApp, Long> {
}
