package redot.redot_server.domain.redot.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.redot.admin.service.AdminRedotAppService;
import redot.redot_server.domain.cms.redotapp.dto.RedotAppCreateRequest;
import redot.redot_server.domain.cms.redotapp.dto.RedotAppInfoResponse;

@RestController
@RequestMapping("/api/v1/admin/app")
@RequiredArgsConstructor
public class AdminRedotAppController {

    private final AdminRedotAppService redotAppService;

    @PostMapping
    public ResponseEntity<RedotAppInfoResponse> createRedotApp(@Valid @RequestBody RedotAppCreateRequest request) {
        return ResponseEntity.ok(redotAppService.createRedotApp(request));
    }
}
