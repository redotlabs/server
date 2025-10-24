package redot.redot_server.domain.cms.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import redot.redot_server.domain.admin.entity.Domain;
import redot.redot_server.domain.admin.exception.DomainErrorCode;
import redot.redot_server.domain.admin.exception.DomainException;
import redot.redot_server.domain.admin.repository.DomainRepository;
import redot.redot_server.domain.cms.dto.SiteSettingResponse;
import redot.redot_server.domain.cms.dto.SiteSettingUpdateRequest;
import redot.redot_server.domain.cms.entity.SiteSetting;
import redot.redot_server.domain.cms.exception.SiteSettingErrorCode;
import redot.redot_server.domain.cms.exception.SiteSettingException;
import redot.redot_server.domain.cms.repository.SiteSettingRepository;
import redot.redot_server.domain.cms.util.LogoPathGenerator;
import redot.redot_server.global.s3.dto.UploadedImageUrlResponse;
import redot.redot_server.global.s3.util.S3Manager;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteSettingService {

    private final SiteSettingRepository siteSettingRepository;
    private final DomainRepository domainRepository;
    private final S3Manager s3Manager;

    @Transactional
    public SiteSettingResponse updateSiteSetting(Long customerId, SiteSettingUpdateRequest request) {
        SiteSetting siteSetting = siteSettingRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new SiteSettingException(SiteSettingErrorCode.SITE_SETTING_NOT_FOUND));
        Domain domain = domainRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new DomainException(DomainErrorCode.DOMAIN_NOT_FOUND));

        if(isCustomDomainExists(request, domain)) {
            throw new DomainException(DomainErrorCode.CUSTOM_DOMAIN_ALREADY_EXISTS);
        }

        siteSetting.updateSiteName(request.siteName());
        siteSetting.updateLogoUrl(request.logoUrl());
        siteSetting.updateGaInfo(request.gaInfo());
        domain.updateCustomDomain(request.customDomain());

        deleteOldLogoIfChanged(siteSetting, request.logoUrl());

        return SiteSettingResponse.fromEntity(siteSetting, domain.getCustomDomain());
    }

    public UploadedImageUrlResponse uploadLogoImage(Long customerId, MultipartFile logoFile) {
        if (logoFile == null || logoFile.isEmpty()) {
            throw new SiteSettingException(SiteSettingErrorCode.LOGO_FILE_REQUIRED);
        }

        String logoUrl = s3Manager.uploadFile(
                logoFile,
                LogoPathGenerator.generateLogoPath(customerId, logoFile.getOriginalFilename())
        );

        return new UploadedImageUrlResponse(logoUrl);
    }

    private boolean isCustomDomainExists(SiteSettingUpdateRequest request, Domain domain) {
        return !domain.getCustomDomain().equals(request.customDomain()) && domainRepository.existsByCustomDomain(
                request.customDomain());
    }

    private void deleteOldLogoIfChanged(SiteSetting siteSetting, String newLogoUrl) {
        String currentLogoUrl = siteSetting.getLogoUrl();
        if(currentLogoUrl == null) {
            return;
        }
        if (newLogoUrl == null || !newLogoUrl.equals(currentLogoUrl)) {
            s3Manager.deleteFile(currentLogoUrl);
        }
    }

    public SiteSettingResponse getSiteSetting(Long customerId) {
        SiteSetting siteSetting = siteSettingRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new SiteSettingException(SiteSettingErrorCode.SITE_SETTING_NOT_FOUND));
        Domain domain = domainRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new DomainException(DomainErrorCode.DOMAIN_NOT_FOUND));

        return SiteSettingResponse.fromEntity(siteSetting, domain.getCustomDomain());
    }
}
