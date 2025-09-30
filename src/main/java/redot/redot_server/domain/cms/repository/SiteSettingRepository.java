package redot.redot_server.domain.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.cms.entity.SiteSetting;

public interface SiteSettingRepository extends JpaRepository<SiteSetting, Long> {
}
