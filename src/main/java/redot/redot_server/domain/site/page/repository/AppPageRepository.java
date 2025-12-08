package redot.redot_server.domain.site.page.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.site.page.entity.AppPage;

public interface AppPageRepository extends JpaRepository<AppPage, Long> {
}
