package redot.redot_server.domain.admin.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springdoc.core.annotations.ParameterObject;
import redot.redot_server.domain.admin.dto.request.AdminCreateRequest;
import redot.redot_server.domain.admin.dto.request.AdminResetPasswordRequest;
import redot.redot_server.domain.admin.dto.request.AdminUpdateRequest;
import redot.redot_server.domain.admin.dto.response.AdminResponse;
import redot.redot_server.global.s3.dto.UploadedImageUrlResponse;
import redot.redot_server.global.security.principal.JwtPrincipal;
import redot.redot_server.global.util.dto.response.PageResponse;

@Tag(name = "Admin", description = "Redot 관리자 API")
public interface AdminControllerDocs {

    @Operation(summary = "관리자 단건 조회", description = "지정한 ID의 관리자 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = AdminResponse.class)))
    ResponseEntity<AdminResponse> getAdminInfo(@Parameter(description = "관리자 ID", example = "1") Long adminId);

    @Operation(summary = "관리자 생성", description = "새로운 관리자를 생성합니다.")
    @ApiResponse(responseCode = "200", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = AdminResponse.class)))
    ResponseEntity<AdminResponse> createAdmin(@Valid AdminCreateRequest request);

    @Operation(summary = "관리자 목록 조회", description = "`page`, `size`, `sort` 파라미터를 사용해 페이징/정렬하며 `sort=필드명,정렬방향` (예: `sort=createdAt,desc`) 형식을 따릅니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    ResponseEntity<PageResponse<AdminResponse>> getAdminInfoList(@Parameter(description = "기본적으로 page=0, size=20이며 sort 파라미터는 `createdAt,desc`와 같이 전달합니다.")
                                                                 @ParameterObject Pageable pageable);

    @Operation(summary = "관리자 정보 수정", description = "지정된 관리자의 프로필 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(schema = @Schema(implementation = AdminResponse.class)))
    ResponseEntity<AdminResponse> updateAdmin(@Parameter(description = "관리자 ID", example = "1") Long adminId,
                                              @Valid AdminUpdateRequest request);

    @Operation(summary = "관리자 비밀번호 초기화", description = "관리자의 비밀번호를 재설정합니다.")
    @ApiResponse(responseCode = "204", description = "재설정 완료")
    ResponseEntity<Void> resetAdminPassword(@Parameter(description = "관리자 ID", example = "1") Long adminId,
                                            @Valid AdminResetPasswordRequest request);

    @Operation(summary = "다른 관리자 삭제", description = "현재 로그인한 관리자가 다른 관리자를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 완료")
    ResponseEntity<Void> deleteAdmin(@Parameter(description = "삭제 대상 관리자 ID", example = "2") Long adminId,
                                     @Parameter(hidden = true) JwtPrincipal jwtPrincipal);

    @Operation(summary = "현재 관리자 탈퇴", description = "본인의 계정을 삭제하고 토큰 쿠키를 제거합니다.")
    @ApiResponse(responseCode = "204", description = "탈퇴 완료")
    ResponseEntity<Void> deleteCurrentAdmin(@Parameter(hidden = true) HttpServletRequest request,
                                            @Parameter(hidden = true) JwtPrincipal jwtPrincipal);

    @Operation(summary = "관리자 프로필 이미지 업로드", description = "관리자 프로필 이미지를 업로드하고 경로를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "업로드 성공",
            content = @Content(schema = @Schema(implementation = UploadedImageUrlResponse.class)))
    ResponseEntity<UploadedImageUrlResponse> uploadProfileImage(@Parameter(hidden = true) JwtPrincipal jwtPrincipal,
                                                                @NotNull MultipartFile image);
}
