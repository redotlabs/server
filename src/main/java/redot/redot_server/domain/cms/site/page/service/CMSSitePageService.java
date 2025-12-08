package redot.redot_server.domain.cms.site.page.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.cms.site.page.dto.request.AppPageCreateRequest;
import redot.redot_server.domain.cms.site.page.dto.request.AppPageRetainRequest;
import redot.redot_server.domain.cms.site.page.dto.request.AppVersionCreateRequest;
import redot.redot_server.domain.cms.site.page.dto.response.AppVersionPageSummaryResponse;
import redot.redot_server.domain.cms.site.page.dto.response.AppVersionSummaryResponse;
import redot.redot_server.domain.cms.site.page.service.dto.AppVersionPageSummaryWithVersionResponse;
import redot.redot_server.domain.cms.site.page.exception.CMSSitePageErrorCode;
import redot.redot_server.domain.cms.site.page.exception.CMSSitePageException;
import redot.redot_server.domain.redot.app.entity.RedotApp;
import redot.redot_server.domain.redot.app.exception.RedotAppErrorCode;
import redot.redot_server.domain.redot.app.exception.RedotAppException;
import redot.redot_server.domain.redot.app.repository.RedotAppRepository;
import redot.redot_server.domain.site.page.dto.response.AppPageResponse;
import redot.redot_server.domain.site.page.entity.AppPage;
import redot.redot_server.domain.site.page.entity.AppVersion;
import redot.redot_server.domain.site.page.entity.AppVersionPage;
import redot.redot_server.domain.site.page.entity.AppVersionStatus;
import redot.redot_server.domain.site.page.repository.AppPageRepository;
import redot.redot_server.domain.site.page.repository.AppVersionPageRepository;
import redot.redot_server.domain.site.page.repository.AppVersionRepository;
import redot.redot_server.global.util.dto.response.PageResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CMSSitePageService {

    private final AppVersionRepository appVersionRepository;
    private final AppVersionPageRepository appVersionPageRepository;
    private final AppPageRepository appPageRepository;
    private final RedotAppRepository redotAppRepository;

    public PageResponse<AppVersionSummaryResponse> getAppVersions(Long redotAppId, AppVersionStatus status,
                                                                  Pageable pageable) {
        Page<AppVersion> versions = findVersions(redotAppId, status, pageable);
        Map<Long, List<AppVersionPageSummaryResponse>> pagesByVersionId = fetchPageSummaries(versions.getContent());
        return PageResponse.from(versions.map(version ->
                AppVersionSummaryResponse.from(
                        version,
                        pagesByVersionId.getOrDefault(version.getId(), Collections.emptyList())
                )));
    }

    public AppPageResponse getPage(Long redotAppId, Long pageId) {
        if (!appVersionPageRepository.existsByAppVersion_RedotApp_IdAndAppPage_Id(redotAppId, pageId)) {
            throw new CMSSitePageException(CMSSitePageErrorCode.PAGE_NOT_BELONG_TO_APP);
        }
        AppPage page = appPageRepository.findById(pageId)
                .orElseThrow(() -> new CMSSitePageException(CMSSitePageErrorCode.PAGE_NOT_FOUND));
        return AppPageResponse.from(page);
    }

    @Transactional
    public AppVersionSummaryResponse createAppVersion(Long redotAppId, AppVersionCreateRequest request) {
        RedotApp redotApp = loadRedotApp(redotAppId);
        validateStatus(request.status());

        AppVersion savedVersion = createVersion(redotAppId, redotApp, request);
        List<AppVersionPage> versionPages = buildVersionPages(redotAppId, savedVersion, request);
        saveVersionPages(versionPages);

        return AppVersionSummaryResponse.from(savedVersion, fetchPagesForVersion(savedVersion.getId()));
    }

    private RedotApp loadRedotApp(Long redotAppId) {
        return redotAppRepository.findById(redotAppId)
                .orElseThrow(() -> new RedotAppException(RedotAppErrorCode.REDOT_APP_NOT_FOUND));
    }

    private AppVersion createVersion(Long redotAppId,
                                     RedotApp redotApp,
                                     AppVersionCreateRequest request) {
        boolean publishRequested = request.status() == AppVersionStatus.PUBLISHED;
        if (publishRequested) {
            appVersionRepository.findFirstByRedotAppIdAndStatusOrderByIdDesc(redotAppId, AppVersionStatus.PUBLISHED)
                    .ifPresent(existing -> existing.changeStatus(AppVersionStatus.PREVIOUS));
        }

        AppVersionStatus initialStatus = publishRequested ? AppVersionStatus.DRAFT : request.status();
        AppVersion newVersion = AppVersion.create(redotApp, initialStatus, request.remark());
        AppVersion savedVersion = saveVersion(newVersion);
        if (publishRequested) {
            savedVersion.changeStatus(AppVersionStatus.PUBLISHED);
            saveVersion(savedVersion);
        }
        return savedVersion;
    }

    private AppVersion saveVersion(AppVersion version) {
        try {
            return appVersionRepository.save(version);
        } catch (DataIntegrityViolationException e) {
            throw new CMSSitePageException(CMSSitePageErrorCode.PUBLISHED_VERSION_ALREADY_EXISTS);
        }
    }

    private List<AppVersionPage> buildVersionPages(Long redotAppId,
                                                   AppVersion version,
                                                   AppVersionCreateRequest request) {
        Set<String> usedPaths = new HashSet<>();
        List<AppVersionPage> versionPages = new ArrayList<>();
        versionPages.addAll(retainedPages(redotAppId, version, request.retained(), usedPaths));
        versionPages.addAll(addedPages(version, request.added(), usedPaths));
        return versionPages;
    }

    private List<AppVersionPage> retainedPages(Long redotAppId,
                                               AppVersion version,
                                               List<AppPageRetainRequest> retained,
                                               Set<String> usedPaths) {
        if (retained == null || retained.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> ids = retained.stream()
                .map(AppPageRetainRequest::id)
                .filter(Objects::nonNull)
                .toList();
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, AppPage> pagesById = appPageRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(AppPage::getId, page -> page));

        List<AppVersionPage> result = new ArrayList<>();
        for (Long id : ids) {
            AppPage page = pagesById.get(id);
            if (page == null) {
                throw new CMSSitePageException(CMSSitePageErrorCode.RETAINED_PAGE_NOT_FOUND);
            }
            Long ownerAppId = page.getRedotApp() != null ? page.getRedotApp().getId() : null;
            if (ownerAppId == null || !ownerAppId.equals(redotAppId)) {
                throw new CMSSitePageException(CMSSitePageErrorCode.PAGE_NOT_BELONG_TO_APP);
            }
            addPageMapping(result, version, page, page.getPath(), usedPaths);
        }
        return result;
    }

    private List<AppVersionPage> addedPages(AppVersion version,
                                            List<AppPageCreateRequest> added,
                                            Set<String> usedPaths) {
        if (added == null || added.isEmpty()) {
            return Collections.emptyList();
        }
        List<AppVersionPage> result = new ArrayList<>();
        RedotApp redotApp = version.getRedotApp();
        for (AppPageCreateRequest request : added) {
            AppPage page = AppPage.create(
                    redotApp,
                    request.content(),
                    request.path(),
                    request.isProtected(),
                    request.description(),
                    request.title()
            );
            AppPage savedPage = appPageRepository.save(page);
            addPageMapping(result, version, savedPage, savedPage.getPath(), usedPaths);
        }
        return result;
    }

    private void addPageMapping(List<AppVersionPage> accumulator,
                                AppVersion version,
                                AppPage page,
                                String path,
                                Set<String> usedPaths) {
        if (!usedPaths.add(path)) {
            throw new CMSSitePageException(CMSSitePageErrorCode.PAGE_PATH_DUPLICATED);
        }
        accumulator.add(AppVersionPage.create(version, page));
    }

    private void saveVersionPages(List<AppVersionPage> versionPages) {
        if (versionPages.isEmpty()) {
            return;
        }
        try {
            appVersionPageRepository.saveAll(versionPages);
        } catch (DataIntegrityViolationException e) {
            throw new CMSSitePageException(CMSSitePageErrorCode.PAGE_PATH_DUPLICATED);
        }
    }

    private void validateStatus(AppVersionStatus status) {
        if (status == null || status == AppVersionStatus.PREVIOUS) {
            throw new CMSSitePageException(CMSSitePageErrorCode.INVALID_VERSION_STATUS);
        }
    }

    private Map<Long, List<AppVersionPageSummaryResponse>> fetchPageSummaries(List<AppVersion> versions) {
        if (versions == null || versions.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> ids = versions.stream()
                .map(AppVersion::getId)
                .filter(Objects::nonNull)
                .toList();
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return appVersionPageRepository.findSummariesByAppVersionIds(ids).stream()
                .collect(Collectors.groupingBy(
                        AppVersionPageSummaryWithVersionResponse::appVersionId,
                        Collectors.mapping(AppVersionPageSummaryWithVersionResponse::toSummary, Collectors.toList())
                ));
    }

    private List<AppVersionPageSummaryResponse> fetchPagesForVersion(Long versionId) {
        return appVersionPageRepository.findSummariesByAppVersionId(versionId);
    }

    private Page<AppVersion> findVersions(Long redotAppId,
                                          AppVersionStatus status,
                                          Pageable pageable) {
        if (status == null) {
            return appVersionRepository.findByRedotAppId(redotAppId, pageable);
        }
        return appVersionRepository.findByRedotAppIdAndStatus(redotAppId, status, pageable);
    }

}
