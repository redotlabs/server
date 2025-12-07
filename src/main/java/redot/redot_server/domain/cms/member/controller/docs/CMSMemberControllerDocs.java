package redot.redot_server.domain.cms.member.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springdoc.core.annotations.ParameterObject;
import redot.redot_server.domain.cms.member.dto.request.CMSMemberCreateRequest;
import redot.redot_server.domain.cms.member.dto.request.CMSMemberRoleRequest;
import redot.redot_server.domain.cms.member.dto.request.CMSMemberSearchCondition;
import redot.redot_server.domain.cms.member.dto.request.CMSMemberUpdateRequest;
import redot.redot_server.domain.cms.member.dto.response.CMSMemberResponse;
import redot.redot_server.global.s3.dto.UploadedImageUrlResponse;
import redot.redot_server.global.security.principal.JwtPrincipal;
import redot.redot_server.global.util.dto.response.PageResponse;

@Tag(name = "CMS Member", description = "CMS 멤버 관리 API")
public interface CMSMemberControllerDocs {

    @Operation(summary = "CMS 멤버 생성", description = "CMS 앱에 새로운 멤버를 추가합니다.")
    @ApiResponse(responseCode = "200", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = CMSMemberResponse.class)))
    ResponseEntity<CMSMemberResponse> createCMSMember(@Parameter(hidden = true) Long redotAppId,
                                                      CMSMemberCreateRequest request);

    @Operation(summary = "CMS 멤버 단건 조회", description = "멤버 ID 기준으로 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = CMSMemberResponse.class)))
    ResponseEntity<CMSMemberResponse> getCMSMemberInfo(@Parameter(hidden = true) Long redotAppId,
                                                       @Parameter(description = "멤버 ID", example = "1") Long memberId);

    @Operation(summary = "CMS 멤버 목록 조회", description = "`name`, `email`, `role` 검색 조건과 `page`/`size`/`sort=createdAt,desc` 파라미터로 멤버를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    ResponseEntity<PageResponse<CMSMemberResponse>> getCMSMemberList(@Parameter(hidden = true) Long redotAppId,
                                                                     @ParameterObject CMSMemberSearchCondition searchCondition,
                                                                     @Parameter(description = "기본 정렬 createdAt DESC")
                                                                     @ParameterObject Pageable pageable);

    @Operation(summary = "CMS 멤버 권한 변경", description = "멤버 권한을 관리자/멤버로 변경합니다.")
    @ApiResponse(responseCode = "200", description = "변경 성공",
            content = @Content(schema = @Schema(implementation = CMSMemberResponse.class)))
    ResponseEntity<CMSMemberResponse> changeCMSMemberRole(@Parameter(hidden = true) Long redotAppId,
                                                          @Parameter(description = "멤버 ID", example = "1") Long memberId,
                                                          CMSMemberRoleRequest request);

    @Operation(summary = "CMS 멤버 정보 수정", description = "본인 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(schema = @Schema(implementation = CMSMemberResponse.class)))
    ResponseEntity<CMSMemberResponse> updateCMSMember(@Parameter(hidden = true) Long redotAppId,
                                                      @Parameter(hidden = true) JwtPrincipal jwtPrincipal,
                                                      CMSMemberUpdateRequest request);

    @Operation(summary = "CMS 멤버 삭제", description = "특정 멤버를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 완료")
    ResponseEntity<Void> deleteCMSMember(@Parameter(hidden = true) Long redotAppId,
                                         @Parameter(hidden = true) JwtPrincipal jwtPrincipal,
                                         @Parameter(description = "멤버 ID", example = "1") Long memberId);

    @Operation(summary = "현재 CMS 멤버 탈퇴", description = "본인 계정을 삭제하고 토큰 쿠키를 제거합니다.")
    @ApiResponse(responseCode = "204", description = "탈퇴 완료")
    ResponseEntity<Void> deleteCurrentCMSMember(@Parameter(hidden = true) HttpServletRequest request,
                                                @Parameter(hidden = true) Long redotAppId,
                                                @Parameter(hidden = true) JwtPrincipal jwtPrincipal);

    @Operation(summary = "CMS 멤버 프로필 이미지 업로드", description = "CMS 멤버 이미지 파일을 업로드합니다.")
    @ApiResponse(responseCode = "200", description = "업로드 성공",
            content = @Content(schema = @Schema(implementation = UploadedImageUrlResponse.class)))
    ResponseEntity<UploadedImageUrlResponse> uploadProfileImage(@Parameter(hidden = true) Long redotAppId,
                                                                @Parameter(hidden = true) JwtPrincipal jwtPrincipal,
                                                                MultipartFile image);
}
