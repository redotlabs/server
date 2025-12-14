package redot.redot_server.domain.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springdoc.core.annotations.ParameterObject;
import redot.redot_server.domain.admin.controller.docs.AdminRedotMemberControllerDocs;
import redot.redot_server.domain.admin.dto.AdminRedotMemberSearchCondition;
import redot.redot_server.domain.admin.dto.request.AdminRedotMemberUpdateRequest;
import redot.redot_server.domain.admin.dto.response.AdminRedotMemberResponse;
import redot.redot_server.domain.admin.service.AdminRedotMemberService;
import redot.redot_server.global.util.dto.response.PageResponse;

@RestController
@RequestMapping("/api/v1/redot/admin/members")
@RequiredArgsConstructor
public class AdminRedotMemberController implements AdminRedotMemberControllerDocs {

    private final AdminRedotMemberService adminRedotMemberService;

    @GetMapping
    @Override
    public ResponseEntity<PageResponse<AdminRedotMemberResponse>> getRedotMembers(
            @ParameterObject AdminRedotMemberSearchCondition searchCondition,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(adminRedotMemberService.getRedotMembers(searchCondition, pageable));
    }

    @PutMapping("/{memberId}")
    @Override
    public ResponseEntity<AdminRedotMemberResponse> updateRedotMember(
            @PathVariable("memberId") Long memberId,
            @Valid @RequestBody AdminRedotMemberUpdateRequest request
    ) {
        return ResponseEntity.ok(adminRedotMemberService.updateRedotMember(memberId, request));
    }

    @DeleteMapping("/{memberId}")
    @Override
    public ResponseEntity<Void> deleteRedotMember(@PathVariable("memberId") Long memberId) {
        adminRedotMemberService.deleteRedotMember(memberId);
        return ResponseEntity.noContent().build();
    }
}
