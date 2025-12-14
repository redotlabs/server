package redot.redot_server.domain.cms.site.setting.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import redot.redot_server.domain.cms.site.setting.dto.request.SiteSettingUpdateRequest;
import redot.redot_server.domain.cms.site.setting.dto.response.SiteSettingResponse;
import redot.redot_server.global.s3.dto.UploadedImageUrlResponse;

@Tag(name = "Site Setting", description = "사이트 설정 API")
public interface SiteSettingControllerDocs {

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "사이트 설정 수정", description = "CMS 앱의 사이트 기본 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(schema = @Schema(implementation = SiteSettingResponse.class)))
    ResponseEntity<SiteSettingResponse> updateSiteSetting(@Parameter(hidden = true) Long redotAppId,
                                                          @Valid SiteSettingUpdateRequest request);

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "사이트 로고 업로드", description = "사이트 로고 이미지를 업로드합니다.")
    @ApiResponse(responseCode = "200", description = "업로드 성공",
            content = @Content(schema = @Schema(implementation = UploadedImageUrlResponse.class)))
    ResponseEntity<UploadedImageUrlResponse> uploadLogoImage(@Parameter(hidden = true) Long redotAppId,
                                                             @NotNull MultipartFile logoFile);

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "사이트 설정 조회", description = "현재 CMS 앱의 사이트 설정을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = SiteSettingResponse.class)))
    ResponseEntity<SiteSettingResponse> getSiteSetting(@Parameter(hidden = true) Long redotAppId);
}
