package redot.redot_server.domain.cms.redotapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.redot.admin.entity.Domain;
import redot.redot_server.domain.redot.admin.exception.DomainErrorCode;
import redot.redot_server.domain.redot.admin.exception.DomainException;
import redot.redot_server.domain.redot.admin.repository.DomainRepository;
import redot.redot_server.domain.redot.admin.repository.StyleInfoRepository;
import redot.redot_server.domain.redot.member.dto.RedotMemberResponse;
import redot.redot_server.domain.redot.member.entity.RedotMember;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedotAppService {

    private final RedotAppRepository redotAppRepository;
    private final DomainRepository domainRepository;
    private final SiteSettingRepository siteSettingRepository;
    private final StyleInfoRepository styleInfoRepository;

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

}
