package redot.redot_server.domain.site.page.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.site.page.controller.docs.SitePageControllerDocs;
import redot.redot_server.domain.site.page.dto.response.AppPageResponse;
import redot.redot_server.domain.site.page.service.SitePageService;
import redot.redot_server.global.redotapp.resolver.annotation.CurrentRedotApp;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/app/site/pages")
public class SitePageController implements SitePageControllerDocs {

    private final SitePageService sitePageService;

    @GetMapping
    @Override
    public ResponseEntity<AppPageResponse> getSitePages(@CurrentRedotApp Long appId, @RequestParam(name = "path") String path) {
        return ResponseEntity.ok(sitePageService.getSitePages(appId, path));
    }
}
