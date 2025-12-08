package redot.redot_server.domain.site.page.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.site.page.dto.response.AppPageResponse;
import redot.redot_server.domain.site.page.entity.AppPage;
import redot.redot_server.domain.site.page.entity.AppVersion;
import redot.redot_server.domain.site.page.entity.AppVersionPage;
import redot.redot_server.domain.site.page.entity.AppVersionStatus;
import redot.redot_server.domain.site.page.exception.SitePageErrorCode;
import redot.redot_server.domain.site.page.exception.SitePageException;
import redot.redot_server.domain.site.page.repository.AppVersionPageRepository;
import redot.redot_server.domain.site.page.repository.AppVersionRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SitePageService {

    private final AppVersionRepository appVersionRepository;
    private final AppVersionPageRepository appVersionPageRepository;

    public AppPageResponse getSitePages(Long appId, String path) {
        AppVersion publishedVersion = appVersionRepository.findByRedotAppIdAndStatus(appId, AppVersionStatus.PUBLISHED)
                .orElseThrow(() -> new SitePageException(SitePageErrorCode.PUBLISHED_VERSION_NOT_FOUND));

        AppPage page = appVersionPageRepository.findByAppVersionIdAndAppPage_Path(publishedVersion.getId(), path)
                .map(AppVersionPage::getAppPage)
                .orElseThrow(() -> new SitePageException(SitePageErrorCode.PAGE_NOT_FOUND));

        return AppPageResponse.from(page);
    }
}
