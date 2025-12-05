package redot.redot_server.domain.site.setting.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.site.setting.entity.SiteSetting;

public interface SiteSettingRepository extends JpaRepository<SiteSetting, Long> {
    Optional<SiteSetting> findByRedotAppId(Long redotAppId);
}
