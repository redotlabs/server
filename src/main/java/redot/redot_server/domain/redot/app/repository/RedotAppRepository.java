package redot.redot_server.domain.redot.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.redot.app.entity.RedotApp;

public interface RedotAppRepository extends JpaRepository<RedotApp, Long> {
    Page<RedotApp> findByOwnerId(Long ownerId, Pageable pageable);
}
