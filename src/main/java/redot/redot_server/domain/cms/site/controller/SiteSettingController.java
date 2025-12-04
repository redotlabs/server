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
import redot.redot_server.domain.cms.site.dto.response.SiteSettingResponse;
import redot.redot_server.domain.cms.site.dto.request.SiteSettingUpdateRequest;
import redot.redot_server.domain.cms.site.service.SiteSettingService;
import redot.redot_server.global.redotapp.resolver.annotation.CurrentRedotApp;
import redot.redot_server.global.s3.dto.UploadedImageUrlResponse;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/app/cms/site-setting")
public class SiteSettingController {

    private final SiteSettingService siteSettingService;

    @PutMapping
    public ResponseEntity<SiteSettingResponse> updateSiteSetting(
            @CurrentRedotApp Long redotAppId,
            @RequestBody SiteSettingUpdateRequest request
    ) {
        SiteSettingResponse siteSettingResponse = siteSettingService.updateSiteSetting(redotAppId, request);
        return ResponseEntity.ok(siteSettingResponse);
    }

    @PostMapping("/upload-logo")
    public ResponseEntity<UploadedImageUrlResponse> uploadLogoImage(
            @CurrentRedotApp Long redotAppId,
            @RequestPart("logo") @NotNull(message = "업로드할 로고 파일을 선택해주세요.") MultipartFile logoFile
    ) {
        return ResponseEntity.ok(siteSettingService.uploadLogoImage(redotAppId, logoFile));
    }

    @GetMapping
    public ResponseEntity<SiteSettingResponse> getSiteSetting(
            @CurrentRedotApp Long redotAppId
    ) {
        SiteSettingResponse siteSettingResponse = siteSettingService.getSiteSetting(redotAppId);
        return ResponseEntity.ok(siteSettingResponse);
    }
}
