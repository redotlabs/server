package redot.redot_server.domain.site.style.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.site.style.entity.StyleInfo;

public interface StyleInfoRepository extends JpaRepository<StyleInfo, Long> {
    Optional<StyleInfo> findByRedotApp_Id(Long redotAppId);
}
