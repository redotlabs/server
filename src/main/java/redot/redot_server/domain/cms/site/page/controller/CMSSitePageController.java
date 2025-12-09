package redot.redot_server.domain.cms.site.page.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.cms.site.page.controller.docs.CMSSitePageControllerDocs;
import redot.redot_server.domain.cms.site.page.dto.request.AppVersionCreateRequest;
import redot.redot_server.domain.cms.site.page.dto.response.AppVersionSummaryResponse;
import redot.redot_server.domain.cms.site.page.service.CMSSitePageService;
import redot.redot_server.domain.site.page.dto.response.AppPageResponse;
import redot.redot_server.domain.site.page.entity.AppVersionStatus;
import redot.redot_server.global.redotapp.resolver.annotation.CurrentRedotApp;
import redot.redot_server.global.util.dto.response.PageResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/app/cms/pages")
public class CMSSitePageController implements CMSSitePageControllerDocs {

    private final CMSSitePageService cmsSitePageService;

    @GetMapping("/versions")
    @Override
    public ResponseEntity<PageResponse<AppVersionSummaryResponse>> getAppVersions(@CurrentRedotApp Long redotAppId,
                                                                                  @RequestParam(name = "status", required = false) AppVersionStatus status,
                                                                                  @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                                                  Pageable pageable) {
        return ResponseEntity.ok(cmsSitePageService.getAppVersions(redotAppId, status, pageable));
    }

    @GetMapping("/{pageId}")
    @Override
    public ResponseEntity<AppPageResponse> getPage(@CurrentRedotApp Long redotAppId,
                                                   @PathVariable(name = "pageId") Long pageId) {
        return ResponseEntity.ok(cmsSitePageService.getPage(redotAppId, pageId));
    }

    @PostMapping("/versions")
    @Override
    public ResponseEntity<AppVersionSummaryResponse> createVersion(@CurrentRedotApp Long redotAppId,
                                                                   @Valid @RequestBody AppVersionCreateRequest request) {
        return ResponseEntity.ok(cmsSitePageService.createAppVersion(redotAppId, request));
    }
}
