package redot.redot_server.domain.cms.redotapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.redot.admin.entity.Domain;
import redot.redot_server.domain.redot.admin.exception.DomainErrorCode;
import redot.redot_server.domain.redot.admin.exception.DomainException;
import redot.redot_server.domain.redot.admin.repository.DomainRepository;
import redot.redot_server.domain.redot.admin.repository.StyleInfoRepository;
import redot.redot_server.domain.redot.admin.util.SubDomainNameGenerator;
import redot.redot_server.domain.redot.member.dto.RedotMemberResponse;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.domain.auth.exception.AuthErrorCode;
import redot.redot_server.domain.auth.exception.AuthException;
import redot.redot_server.domain.cms.redotapp.dto.RedotAppCreateRequest;
import redot.redot_server.domain.cms.redotapp.dto.RedotAppInfoResponse;
import redot.redot_server.domain.cms.redotapp.dto.RedotAppResponse;
import redot.redot_server.domain.cms.site.dto.SiteSettingResponse;
import redot.redot_server.domain.cms.style.dto.StyleInfoResponse;
import redot.redot_server.domain.cms.redotapp.entity.RedotApp;
import redot.redot_server.domain.cms.site.entity.SiteSetting;
import redot.redot_server.domain.cms.style.entity.StyleInfo;
import redot.redot_server.domain.cms.redotapp.exception.RedotAppErrorCode;
import redot.redot_server.domain.cms.redotapp.exception.RedotAppException;
import redot.redot_server.domain.cms.site.exception.SiteSettingErrorCode;
import redot.redot_server.domain.cms.site.exception.SiteSettingException;
import redot.redot_server.domain.cms.style.exception.StyleInfoErrorCode;
import redot.redot_server.domain.cms.style.exception.StyleInfoException;
import redot.redot_server.domain.cms.redotapp.repository.RedotAppRepository;
import redot.redot_server.domain.cms.site.repository.SiteSettingRepository;
import redot.redot_server.domain.redot.member.repository.RedotMemberRepository;
import redot.redot_server.support.common.dto.PageResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedotAppService {

    private final RedotAppRepository redotAppRepository;
    private final DomainRepository domainRepository;
    private final SiteSettingRepository siteSettingRepository;
    private final StyleInfoRepository styleInfoRepository;
    private final RedotMemberRepository redotMemberRepository;

    public RedotAppInfoResponse getRedotAppInfo(Long redotAppId) {
        RedotApp redotApp = redotAppRepository.findById(redotAppId).orElseThrow(
                () -> new RedotAppException(RedotAppErrorCode.REDOT_APP_NOT_FOUND)
        );

        Domain domain = domainRepository.findByRedotAppId(redotAppId)
                .orElseThrow(() -> new DomainException(DomainErrorCode.DOMAIN_NOT_FOUND));

        StyleInfo styleInfo = styleInfoRepository.findByRedotApp_Id(redotAppId)
                .orElseThrow(() -> new StyleInfoException(StyleInfoErrorCode.STYLE_INFO_NOT_FOUND));

        SiteSetting siteSetting = siteSettingRepository.findByRedotAppId(redotAppId)
                .orElseThrow(() -> new SiteSettingException(SiteSettingErrorCode.SITE_SETTING_NOT_FOUND));

        RedotMember owner = redotApp.getOwner();

        return new RedotAppInfoResponse(
                RedotAppResponse.fromEntity(redotApp),
                SiteSettingResponse.fromEntity(siteSetting, domain),
                StyleInfoResponse.fromEntity(styleInfo),
                RedotMemberResponse.fromNullable(owner)
        );
    }

    public PageResponse<RedotAppInfoResponse> getRedotAppList(Long ownerId, Pageable pageable) {
        Page<RedotApp> redotApps = redotAppRepository.findByOwnerId(ownerId, pageable);

        Page<RedotAppInfoResponse> responsePage = redotApps.map(redotApp -> {
            Domain domain = domainRepository.findByRedotAppId(redotApp.getId())
                    .orElseThrow(() -> new DomainException(DomainErrorCode.DOMAIN_NOT_FOUND));

            StyleInfo styleInfo = styleInfoRepository.findByRedotApp_Id(redotApp.getId())
                    .orElseThrow(() -> new StyleInfoException(StyleInfoErrorCode.STYLE_INFO_NOT_FOUND));

            SiteSetting siteSetting = siteSettingRepository.findByRedotAppId(redotApp.getId())
                    .orElseThrow(() -> new SiteSettingException(SiteSettingErrorCode.SITE_SETTING_NOT_FOUND));

            return new RedotAppInfoResponse(
                    RedotAppResponse.fromEntity(redotApp),
                    SiteSettingResponse.fromEntity(siteSetting, domain),
                    StyleInfoResponse.fromEntity(styleInfo),
                    RedotMemberResponse.fromNullable(redotApp.getOwner())
            );
        });

        return PageResponse.from(responsePage);
    }

    // Admin에서도 동일한 로직이 있음. 이걸 메인으로 어드민에서 재사용하는 방법 고려 필요
    @Transactional
    public RedotAppInfoResponse createRedotApp(RedotAppCreateRequest request, Long currentUserId) {
        RedotMember owner = redotMemberRepository.findById(currentUserId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.REDOT_MEMBER_NOT_FOUND));
        RedotApp redotApp = redotAppRepository.save(RedotApp.create(request.appName(), owner));

        String domainName = SubDomainNameGenerator.generateSubdomain();
        Domain domain = domainRepository.save(Domain.ofRedotApp(domainName, redotApp));

        SiteSetting siteSetting = siteSettingRepository.save(SiteSetting.createDefault(redotApp));

        StyleInfo styleInfo = styleInfoRepository.save(
                StyleInfo.create(
                        request.font(),
                        request.color(),
                        request.theme(),
                        redotApp
                )
        );

        return new RedotAppInfoResponse(
                RedotAppResponse.fromEntity(redotApp),
                SiteSettingResponse.fromEntity(siteSetting, domain),
                StyleInfoResponse.fromEntity(styleInfo),
                RedotMemberResponse.fromNullable(redotApp.getOwner())
        );
    }
}
