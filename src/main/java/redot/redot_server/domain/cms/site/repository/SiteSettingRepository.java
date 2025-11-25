package redot.redot_server.domain.cms.site.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.cms.site.entity.SiteSetting;

public interface SiteSettingRepository extends JpaRepository<SiteSetting, Long> {
    Optional<SiteSetting> findByRedotAppId(Long redotAppId);
}
