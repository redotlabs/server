package redot.redot_server.domain.site.page.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import redot.redot_server.domain.site.page.entity.AppVersion;
import redot.redot_server.domain.site.page.entity.AppVersionStatus;

public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {


    Optional<AppVersion> findByRedotAppIdAndStatus(Long redotAppId, AppVersionStatus status);

    @org.springframework.data.jpa.repository.Query("select av from AppVersion av where av.redotApp.id = :redotAppId and av.status = :status order by av.id desc")
    Optional<AppVersion> findFirstByRedotAppIdAndStatusOrderByIdDesc(@Param("redotAppId") Long redotAppId,
                                                                     @Param("status") AppVersionStatus status);

    Page<AppVersion> findByRedotAppIdAndStatus(Long redotAppId, AppVersionStatus status, Pageable pageable);

    Page<AppVersion> findByRedotAppId(Long redotAppId, Pageable pageable);
}
