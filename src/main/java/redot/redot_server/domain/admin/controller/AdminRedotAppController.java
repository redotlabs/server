package redot.redot_server.domain.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.admin.controller.docs.AdminRedotAppControllerDocs;
import redot.redot_server.domain.admin.service.AdminRedotAppService;
import redot.redot_server.domain.redot.app.dto.request.RedotAppCreateRequest;
import redot.redot_server.domain.redot.app.dto.response.RedotAppInfoResponse;

@RestController
@RequestMapping("/api/v1/redot/admin/app")
@RequiredArgsConstructor
public class AdminRedotAppController implements AdminRedotAppControllerDocs {

    private final AdminRedotAppService redotAppService;

    @PostMapping
    @Override
    public ResponseEntity<RedotAppInfoResponse> createRedotApp(@Valid @RequestBody RedotAppCreateRequest request) {
        return ResponseEntity.ok(redotAppService.createRedotApp(request));
    }
}
