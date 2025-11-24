package redot.redot_server.domain.cms.redotapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.cms.redotapp.dto.RedotAppInfoResponse;
import redot.redot_server.domain.cms.redotapp.service.RedotAppService;
import redot.redot_server.support.redotapp.resolver.annotation.CurrentRedotApp;

@RestController
@RequestMapping("/api/v1/app")
@RequiredArgsConstructor
public class RedotAppController {

    private final RedotAppService redotAppService;

    @GetMapping
    public ResponseEntity<RedotAppInfoResponse> getRedotAppInfo(@CurrentRedotApp Long redotAppId) {
        return ResponseEntity.ok(redotAppService.getRedotAppInfo(redotAppId));
    }
}
