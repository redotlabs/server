package redot.redot_server.domain.cms.site.setting.service;


import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import redot.redot_server.domain.cms.site.setting.dto.request.SiteSettingUpdateRequest;
import redot.redot_server.domain.cms.site.setting.dto.response.SiteSettingResponse;
import redot.redot_server.domain.site.domain.entity.Domain;
import redot.redot_server.domain.site.domain.exception.DomainErrorCode;
import redot.redot_server.domain.site.domain.exception.DomainException;
import redot.redot_server.domain.site.domain.repository.DomainRepository;
import redot.redot_server.domain.site.setting.entity.SiteSetting;
import redot.redot_server.domain.site.setting.exception.SiteSettingErrorCode;
import redot.redot_server.domain.site.setting.exception.SiteSettingException;
import redot.redot_server.domain.site.setting.repository.SiteSettingRepository;
import redot.redot_server.global.s3.dto.UploadedImageUrlResponse;
import redot.redot_server.global.s3.service.ImageStorageService;
import redot.redot_server.global.s3.util.ImageDirectory;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteSettingService {

    private final SiteSettingRepository siteSettingRepository;
    private final DomainRepository domainRepository;
    private final ImageStorageService imageStorageService;

    @Transactional
    public SiteSettingResponse updateSiteSetting(Long redotAppId, SiteSettingUpdateRequest request) {
        SiteSetting siteSetting = siteSettingRepository.findByRedotAppId(redotAppId)
                .orElseThrow(() -> new SiteSettingException(SiteSettingErrorCode.SITE_SETTING_NOT_FOUND));
        Domain domain = domainRepository.findByRedotAppId(redotAppId)
                .orElseThrow(() -> new DomainException(DomainErrorCode.DOMAIN_NOT_FOUND));

        if(isCustomDomainExists(request, domain)) {
            throw new DomainException(DomainErrorCode.CUSTOM_DOMAIN_ALREADY_EXISTS);
        }

        siteSetting.updateSiteName(request.siteName());
        siteSetting.updateLogoUrl(request.logoUrl());
        siteSetting.updateGaInfo(request.gaInfo());
        domain.updateCustomDomain(request.customDomain());

        deleteOldLogoIfChanged(siteSetting, request.logoUrl());

        return SiteSettingResponse.fromEntity(siteSetting, domain);
    }

    public UploadedImageUrlResponse uploadLogoImage(Long redotAppId, MultipartFile logoFile) {
        if (logoFile == null || logoFile.isEmpty()) {
            throw new SiteSettingException(SiteSettingErrorCode.LOGO_FILE_REQUIRED);
        }

        String logoUrl = imageStorageService.upload(ImageDirectory.APP_LOGO, redotAppId, logoFile);

        return new UploadedImageUrlResponse(logoUrl);
    }

    private boolean isCustomDomainExists(SiteSettingUpdateRequest request, Domain domain) {
        String currentCustomDomain = domain.getCustomDomain();
        String requestedCustomDomain = request.customDomain();

        if (Objects.equals(currentCustomDomain, requestedCustomDomain)) {
            return false;
        }

        if (requestedCustomDomain == null) {
            return false;
        }

        return domainRepository.existsByCustomDomain(requestedCustomDomain);
    }

    private void deleteOldLogoIfChanged(SiteSetting siteSetting, String newLogoUrl) {
        String currentLogoUrl = siteSetting.getLogoUrl();
        if(currentLogoUrl == null) {
            return;
        }
        if (newLogoUrl == null || !newLogoUrl.equals(currentLogoUrl)) {
            imageStorageService.delete(currentLogoUrl);
        }
    }

    public SiteSettingResponse getSiteSetting(Long redotAppId) {
        SiteSetting siteSetting = siteSettingRepository.findByRedotAppId(redotAppId)
                .orElseThrow(() -> new SiteSettingException(SiteSettingErrorCode.SITE_SETTING_NOT_FOUND));
        Domain domain = domainRepository.findByRedotAppId(redotAppId)
                .orElseThrow(() -> new DomainException(DomainErrorCode.DOMAIN_NOT_FOUND));

        return SiteSettingResponse.fromEntity(siteSetting, domain);
    }
}
