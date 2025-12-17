package redot.redot_server.domain.admin.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.dto.request.RedotAppInfoSearchCondition;
import redot.redot_server.domain.admin.dto.request.RedotAppStatusUpdateRequest;
import redot.redot_server.domain.cms.site.setting.dto.response.SiteSettingResponse;
import redot.redot_server.domain.cms.site.style.dto.response.StyleInfoResponse;
import redot.redot_server.domain.redot.app.dto.request.RedotAppCreateRequest;
import redot.redot_server.domain.redot.app.dto.response.RedotAppInfoResponse;
import redot.redot_server.domain.redot.app.dto.response.RedotAppResponse;
import redot.redot_server.domain.redot.app.entity.RedotApp;
import redot.redot_server.domain.redot.app.exception.RedotAppErrorCode;
import redot.redot_server.domain.redot.app.exception.RedotAppException;
import redot.redot_server.domain.redot.app.repository.RedotAppRepository;
import redot.redot_server.domain.redot.app.service.RedotAppCreationService;
import redot.redot_server.domain.redot.member.dto.response.RedotMemberResponse;
import redot.redot_server.domain.site.domain.entity.Domain;
import redot.redot_server.domain.site.domain.repository.DomainRepository;
import redot.redot_server.domain.site.setting.entity.SiteSetting;
import redot.redot_server.domain.site.setting.repository.SiteSettingRepository;
import redot.redot_server.domain.site.style.entity.StyleInfo;
import redot.redot_server.domain.site.style.repository.StyleInfoRepository;
import redot.redot_server.global.s3.util.ImageUrlResolver;
import redot.redot_server.global.util.dto.response.PageResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRedotAppService {
    private final RedotAppCreationService redotAppCreationService;
    private final RedotAppRepository redotAppRepository;
    private final DomainRepository domainRepository;
    private final SiteSettingRepository siteSettingRepository;
    private final StyleInfoRepository styleInfoRepository;
    private final ImageUrlResolver imageUrlResolver;

    @Transactional
    public RedotAppInfoResponse createRedotApp(RedotAppCreateRequest request) {
        return redotAppCreationService.createWithoutOwner(request);
    }

    public PageResponse<RedotAppInfoResponse> getRedotAppInfoList(RedotAppInfoSearchCondition searchCondition,
                                                                  Pageable pageable) {
        Page<RedotApp> redotApps = redotAppRepository.findAllBySearchCondition(searchCondition, pageable);

        List<Long> redotAppIds = redotApps.stream()
                .map(RedotApp::getId)
                .toList();

        Map<Long, Domain> domainMap = redotAppIds.isEmpty() ? Map.of()
                : domainRepository.findByRedotAppIdIn(redotAppIds)
                .stream()
                .collect(Collectors.toMap(domain -> domain.getRedotApp().getId(), Function.identity()));

        Map<Long, StyleInfo> styleInfoMap = redotAppIds.isEmpty() ? Map.of()
                : styleInfoRepository.findByRedotApp_IdIn(redotAppIds)
                .stream()
                .collect(Collectors.toMap(style -> style.getRedotApp().getId(), Function.identity()));

        Map<Long, SiteSetting> siteSettingMap = redotAppIds.isEmpty() ? Map.of()
                : siteSettingRepository.findByRedotAppIdIn(redotAppIds)
                .stream()
                .collect(Collectors.toMap(setting -> setting.getRedotApp().getId(), Function.identity()));

        Page<RedotAppInfoResponse> responsePage = redotApps.map(redotApp ->
                toRedotAppInfoResponse(
                        redotApp,
                        domainMap.get(redotApp.getId()),
                        styleInfoMap.get(redotApp.getId()),
                        siteSettingMap.get(redotApp.getId())
                ));

        return PageResponse.from(responsePage);
    }

    private RedotAppInfoResponse toRedotAppInfoResponse(RedotApp redotApp) {
        Domain domain = domainRepository.findByRedotAppId(redotApp.getId()).orElse(null);
        StyleInfo styleInfo = styleInfoRepository.findByRedotApp_Id(redotApp.getId()).orElse(null);
        SiteSetting siteSetting = siteSettingRepository.findByRedotAppId(redotApp.getId()).orElse(null);

        return toRedotAppInfoResponse(redotApp, domain, styleInfo, siteSetting);
    }

    private RedotAppInfoResponse toRedotAppInfoResponse(RedotApp redotApp,
                                                        Domain domain,
                                                        StyleInfo styleInfo,
                                                        SiteSetting siteSetting) {
        SiteSettingResponse siteSettingResponse = (siteSetting != null && domain != null)
                ? SiteSettingResponse.fromEntity(siteSetting, domain, imageUrlResolver)
                : null;

        StyleInfoResponse styleInfoResponse = styleInfo != null
                ? StyleInfoResponse.fromEntity(styleInfo)
                : null;

        return new RedotAppInfoResponse(
                RedotAppResponse.fromEntity(redotApp),
                siteSettingResponse,
                styleInfoResponse,
                RedotMemberResponse.fromNullable(redotApp.getOwner(), imageUrlResolver)
        );
    }

    public RedotAppInfoResponse getRedotAppInfo(Long redotAppId) {
        RedotApp redotApp = redotAppRepository.findById(redotAppId)
                .orElseThrow(() -> new RedotAppException(RedotAppErrorCode.REDOT_APP_NOT_FOUND));

        return toRedotAppInfoResponse(redotApp);
    }

    @Transactional
    public RedotAppInfoResponse updateRedotAppStatus(Long redotAppId, RedotAppStatusUpdateRequest request) {
        RedotApp redotApp = redotAppRepository.findById(redotAppId)
                .orElseThrow(() -> new RedotAppException(RedotAppErrorCode.REDOT_APP_NOT_FOUND));

        redotApp.updateStatus(request.status(), request.remark());

        return toRedotAppInfoResponse(redotApp);
    }
}
