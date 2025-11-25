package redot.redot_server.domain.redot.admin.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.cms.style.entity.StyleInfo;

public interface StyleInfoRepository extends JpaRepository<StyleInfo, Long> {
    Optional<StyleInfo> findByRedotApp_Id(Long redotAppId);
}
