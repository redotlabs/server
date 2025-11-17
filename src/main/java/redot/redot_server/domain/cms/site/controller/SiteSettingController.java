package redot.redot_server.domain.cms.site.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redot.redot_server.domain.cms.site.dto.SiteSettingResponse;
import redot.redot_server.domain.cms.site.dto.SiteSettingUpdateRequest;
import redot.redot_server.domain.cms.site.service.SiteSettingService;
import redot.redot_server.support.customer.resolver.annotation.CurrentCustomer;
import redot.redot_server.support.s3.dto.UploadedImageUrlResponse;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/customer/cms/site-setting")
public class SiteSettingController {

    private final SiteSettingService siteSettingService;

    @PutMapping
    public ResponseEntity<SiteSettingResponse> updateSiteSetting(
            @CurrentCustomer Long customerId,
            @RequestBody SiteSettingUpdateRequest request
    ) {
        SiteSettingResponse siteSettingResponse = siteSettingService.updateSiteSetting(customerId, request);
        return ResponseEntity.ok(siteSettingResponse);
    }

    @PostMapping("/upload-logo")
    public ResponseEntity<UploadedImageUrlResponse> uploadLogoImage(
            @CurrentCustomer Long customerId,
            @RequestPart("logo") @NotNull(message = "업로드할 로고 파일을 선택해주세요.") MultipartFile logoFile
    ) {
        return ResponseEntity.ok(siteSettingService.uploadLogoImage(customerId, logoFile));
    }

    @GetMapping
    public ResponseEntity<SiteSettingResponse> getSiteSetting(
            @CurrentCustomer Long customerId
    ) {
        SiteSettingResponse siteSettingResponse = siteSettingService.getSiteSetting(customerId);
        return ResponseEntity.ok(siteSettingResponse);
    }
}
