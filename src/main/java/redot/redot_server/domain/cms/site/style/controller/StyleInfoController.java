package redot.redot_server.domain.cms.site.style.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.cms.site.style.dto.response.StyleInfoResponse;
import redot.redot_server.domain.cms.site.style.dto.request.StyleInfoUpdateRequest;
import redot.redot_server.domain.cms.site.style.service.StyleInfoService;
import redot.redot_server.global.redotapp.resolver.annotation.CurrentRedotApp;

@RestController
@RequestMapping("/api/v1/app/cms/style-info")
@RequiredArgsConstructor
public class StyleInfoController {
    private final StyleInfoService styleInfoService;

    @GetMapping
    public ResponseEntity<StyleInfoResponse> getStyleInfo(@CurrentRedotApp Long redotAppId) {
        StyleInfoResponse styleInfoResponse = styleInfoService.getStyleInfo(redotAppId);
        return ResponseEntity.ok(styleInfoResponse);
    }

    @PatchMapping
    public ResponseEntity<StyleInfoResponse> updateStyleInfo(@CurrentRedotApp Long redotAppId, @RequestBody StyleInfoUpdateRequest request) {
        return ResponseEntity.ok().body(styleInfoService.updateStyleInfo(redotAppId, request));
    }
}
