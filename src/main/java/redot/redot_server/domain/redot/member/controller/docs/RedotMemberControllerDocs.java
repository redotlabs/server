package redot.redot_server.domain.redot.member.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import redot.redot_server.domain.redot.member.dto.RedotMemberUpdateRequest;
import redot.redot_server.domain.redot.member.dto.response.RedotMemberResponse;
import redot.redot_server.global.s3.dto.UploadedImageUrlResponse;
import redot.redot_server.global.security.principal.JwtPrincipal;

@Tag(name = "Redot Member", description = "Redot 회원 API")
public interface RedotMemberControllerDocs {

    @Operation(summary = "Redot 회원 정보 수정", description = "회원 프로필 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(schema = @Schema(implementation = RedotMemberResponse.class)))
    ResponseEntity<RedotMemberResponse> updateRedotMemberInfo(@Parameter(hidden = true) JwtPrincipal jwtPrincipal,
                                                              RedotMemberUpdateRequest request);

    @Operation(summary = "Redot 회원 프로필 이미지 업로드", description = "회원 프로필 이미지를 업로드합니다.")
    @ApiResponse(responseCode = "200", description = "업로드 성공",
            content = @Content(schema = @Schema(implementation = UploadedImageUrlResponse.class)))
    ResponseEntity<UploadedImageUrlResponse> uploadProfileImage(@Parameter(hidden = true) JwtPrincipal jwtPrincipal,
                                                                MultipartFile image);

    @Operation(summary = "Redot 회원 탈퇴", description = "회원 계정을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "탈퇴 완료")
    ResponseEntity<Void> deleteRedotMember(@Parameter(hidden = true) JwtPrincipal jwtPrincipal);
}
