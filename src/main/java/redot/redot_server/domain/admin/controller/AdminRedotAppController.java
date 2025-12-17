package redot.redot_server.domain.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.admin.controller.docs.AdminRedotAppControllerDocs;
import redot.redot_server.domain.admin.dto.request.RedotAppInfoSearchCondition;
import redot.redot_server.domain.admin.dto.request.RedotAppStatusUpdateRequest;
import redot.redot_server.domain.admin.service.AdminRedotAppService;
import redot.redot_server.domain.redot.app.dto.request.RedotAppCreateRequest;
import redot.redot_server.domain.redot.app.dto.response.RedotAppInfoResponse;
import redot.redot_server.global.util.dto.response.PageResponse;

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

    @GetMapping
    @Override
    public ResponseEntity<PageResponse<RedotAppInfoResponse>> getRedotAppInfoList(
            @ParameterObject RedotAppInfoSearchCondition searchCondition,
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(redotAppService.getRedotAppInfoList(searchCondition, pageable));
    }

    @GetMapping("/{redotAppId}")
    @Override
    public ResponseEntity<RedotAppInfoResponse> getRedotAppInfo(
            @PathVariable("redotAppId") Long redotAppId) {
        return ResponseEntity.ok(redotAppService.getRedotAppInfo(redotAppId));
    }

    @PostMapping("/{redotAppId}/status")
    @Override
    public ResponseEntity<RedotAppInfoResponse> updateRedotAppStatus(
            @PathVariable("redotAppId") Long redotAppId,
            @Valid @RequestBody RedotAppStatusUpdateRequest request) {
        return ResponseEntity.ok(redotAppService.updateRedotAppStatus(redotAppId, request));
    }
}
