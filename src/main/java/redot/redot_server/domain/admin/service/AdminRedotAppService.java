package redot.redot_server.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.dto.request.RedotAppInfoSearchCondition;
import redot.redot_server.domain.cms.site.setting.dto.response.SiteSettingResponse;
import redot.redot_server.domain.cms.site.style.dto.response.StyleInfoResponse;
import redot.redot_server.domain.redot.app.dto.request.RedotAppCreateRequest;
import redot.redot_server.domain.redot.app.dto.response.RedotAppInfoResponse;
import redot.redot_server.domain.redot.app.dto.response.RedotAppResponse;
import redot.redot_server.domain.redot.app.entity.RedotApp;
import redot.redot_server.domain.redot.app.repository.RedotAppRepository;
import redot.redot_server.domain.redot.app.service.RedotAppCreationService;
import redot.redot_server.domain.redot.member.dto.response.RedotMemberResponse;
import redot.redot_server.domain.site.domain.entity.Domain;
import redot.redot_server.domain.site.domain.exception.DomainErrorCode;
import redot.redot_server.domain.site.domain.exception.DomainException;
import redot.redot_server.domain.site.domain.repository.DomainRepository;
import redot.redot_server.domain.site.setting.entity.SiteSetting;
import redot.redot_server.domain.site.setting.exception.SiteSettingErrorCode;
import redot.redot_server.domain.site.setting.exception.SiteSettingException;
import redot.redot_server.domain.site.setting.repository.SiteSettingRepository;
import redot.redot_server.domain.site.style.entity.StyleInfo;
import redot.redot_server.domain.site.style.exception.StyleInfoErrorCode;
import redot.redot_server.domain.site.style.exception.StyleInfoException;
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

        Page<RedotAppInfoResponse> responsePage = redotApps.map(this::toRedotAppInfoResponse);

        return PageResponse.from(responsePage);
    }

    private RedotAppInfoResponse toRedotAppInfoResponse(RedotApp redotApp) {
        Domain domain = domainRepository.findByRedotAppId(redotApp.getId())
                .orElseThrow(() -> new DomainException(DomainErrorCode.DOMAIN_NOT_FOUND));

        StyleInfo styleInfo = styleInfoRepository.findByRedotApp_Id(redotApp.getId())
                .orElseThrow(() -> new StyleInfoException(StyleInfoErrorCode.STYLE_INFO_NOT_FOUND));

        SiteSetting siteSetting = siteSettingRepository.findByRedotAppId(redotApp.getId())
                .orElseThrow(() -> new SiteSettingException(SiteSettingErrorCode.SITE_SETTING_NOT_FOUND));

        return new RedotAppInfoResponse(
                RedotAppResponse.fromEntity(redotApp),
                SiteSettingResponse.fromEntity(siteSetting, domain, imageUrlResolver),
                StyleInfoResponse.fromEntity(styleInfo),
                RedotMemberResponse.fromNullable(redotApp.getOwner(), imageUrlResolver)
        );
    }
}
