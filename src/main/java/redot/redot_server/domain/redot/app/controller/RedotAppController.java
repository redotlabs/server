package redot.redot_server.domain.redot.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import redot.redot_server.domain.redot.app.controller.docs.RedotAppControllerDocs;
import redot.redot_server.domain.redot.app.dto.request.RedotAppCreateManagerRequest;
import redot.redot_server.domain.redot.app.dto.request.RedotAppCreateRequest;
import redot.redot_server.domain.redot.app.dto.response.RedotAppInfoResponse;
import redot.redot_server.domain.redot.app.service.RedotAppService;
import redot.redot_server.global.util.dto.response.PageResponse;
import redot.redot_server.global.redotapp.resolver.annotation.CurrentRedotApp;
import redot.redot_server.global.security.principal.JwtPrincipal;

@RestController
@RequestMapping("/api/v1/app")
@RequiredArgsConstructor
public class RedotAppController implements RedotAppControllerDocs {

    private final RedotAppService redotAppService;

    // subdomain 기반 앱 정보 조회
    @GetMapping("/by-subdomain")
    @Override
    public ResponseEntity<RedotAppInfoResponse> getRedotAppInfo(@CurrentRedotApp Long redotAppId) {
        return ResponseEntity.ok(redotAppService.getRedotAppInfo(redotAppId));
    }

    @GetMapping
    @Override
    public ResponseEntity<PageResponse<RedotAppInfoResponse>> getRedotAppList(
            @AuthenticationPrincipal JwtPrincipal jwtPrincipal,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(redotAppService.getRedotAppList(jwtPrincipal.id(), pageable));
    }

    @PostMapping
    @Override
    public ResponseEntity<RedotAppInfoResponse> createRedotApp(@Valid @RequestBody RedotAppCreateRequest request,
                                                               @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        return ResponseEntity.ok(redotAppService.createRedotApp(request, jwtPrincipal.id()));
    }

    @PostMapping("/{redotAppId}/create-manager")
    @Override
    public ResponseEntity<Void> createManager(@PathVariable("redotAppId") Long redotAppId,
                                              @Valid @RequestBody RedotAppCreateManagerRequest request,
                                              @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        redotAppService.createManager(redotAppId, request, jwtPrincipal.id());
        return ResponseEntity.ok().build();
    }
}
