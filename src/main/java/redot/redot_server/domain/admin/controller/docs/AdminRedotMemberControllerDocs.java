package redot.redot_server.domain.admin.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import redot.redot_server.domain.admin.dto.AdminRedotMemberSearchCondition;
import redot.redot_server.domain.admin.dto.request.AdminRedotMemberUpdateRequest;
import redot.redot_server.domain.admin.dto.response.AdminRedotMemberResponse;
import redot.redot_server.global.util.dto.response.PageResponse;

@Tag(name = "Admin Redot Member", description = "관리자 Redot 회원 관리 API")
public interface AdminRedotMemberControllerDocs {

    @Operation(summary = "Redot 회원 목록 조회", description = "`email`, `name`, `socialProvider`, `status` 조건으로 검색하고 `createdAt` 정렬을 ASC/DESC 로 조정할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    ResponseEntity<PageResponse<AdminRedotMemberResponse>> getRedotMembers(
            @ParameterObject AdminRedotMemberSearchCondition searchCondition,
            @ParameterObject Pageable pageable);

    @Operation(summary = "Redot 회원 정보 수정", description = "이름, 프로필 이미지, 상태를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(schema = @Schema(implementation = AdminRedotMemberResponse.class)))
    ResponseEntity<AdminRedotMemberResponse> updateRedotMember(
            @Parameter(description = "회원 ID", example = "1") Long memberId,
            @Valid AdminRedotMemberUpdateRequest request);

    @Operation(summary = "Redot 회원 삭제", description = "회원 계정을 탈퇴 처리합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    ResponseEntity<Void> deleteRedotMember(@Parameter(description = "회원 ID", example = "1") Long memberId);
}
