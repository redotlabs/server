package redot.redot_server.domain.site.page.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import redot.redot_server.domain.cms.site.page.dto.response.AppVersionPageSummaryResponse;
import redot.redot_server.domain.cms.site.page.service.dto.AppVersionPageSummaryWithVersionResponse;
import redot.redot_server.domain.site.page.entity.AppVersionPage;

public interface AppVersionPageRepository extends JpaRepository<AppVersionPage, Long> {

    Optional<AppVersionPage> findByAppVersionIdAndAppPage_Path(Long appVersionId, String path);

    List<AppVersionPage> findAllByAppVersionId(Long appVersionId);

    @Query("select new redot.redot_server.domain.cms.site.page.dto.response.AppVersionPageSummaryResponse(" +
            " ap.id, ap.path, ap.title, ap.description, ap.isProtected)" +
            " from AppVersionPage avp" +
            " join avp.appPage ap" +
            " where avp.appVersion.id = :appVersionId")
    List<AppVersionPageSummaryResponse> findSummariesByAppVersionId(@Param("appVersionId") Long appVersionId);

    @Query("select new redot.redot_server.domain.cms.site.page.service.dto.AppVersionPageSummaryWithVersionResponse(" +
            " avp.appVersion.id, ap.id, ap.path, ap.title, ap.description, ap.isProtected)" +
            " from AppVersionPage avp" +
            " join avp.appPage ap" +
            " where avp.appVersion.id in :appVersionIds")
    List<AppVersionPageSummaryWithVersionResponse> findSummariesByAppVersionIds(@Param("appVersionIds") List<Long> appVersionIds);

    boolean existsByAppVersion_RedotApp_IdAndAppPage_Id(Long redotAppId, Long appPageId);
}
