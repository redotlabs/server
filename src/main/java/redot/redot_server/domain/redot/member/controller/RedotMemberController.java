package redot.redot_server.domain.redot.member.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redot.redot_server.domain.redot.member.controller.docs.RedotMemberControllerDocs;
import redot.redot_server.domain.redot.member.dto.RedotMemberUpdateRequest;
import redot.redot_server.domain.redot.member.dto.response.RedotMemberResponse;
import redot.redot_server.domain.redot.member.service.RedotMemberService;
import redot.redot_server.global.s3.dto.UploadedImageUrlResponse;
import redot.redot_server.global.security.principal.JwtPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/redot/members")
public class RedotMemberController implements RedotMemberControllerDocs {

    private final RedotMemberService redotMemberService;


    @PutMapping
    @Override
    public ResponseEntity<RedotMemberResponse> updateRedotMemberInfo(
            @AuthenticationPrincipal JwtPrincipal jwtPrincipal,
            @RequestBody RedotMemberUpdateRequest request
    ) {
        RedotMemberResponse redotMemberResponse = redotMemberService.updateRedotMemberInfo(jwtPrincipal.id(), request);
        return ResponseEntity.ok(redotMemberResponse);
    }


    @PostMapping("/upload-profile-image")
    @Override
    public ResponseEntity<UploadedImageUrlResponse> uploadProfileImage(
            @AuthenticationPrincipal JwtPrincipal jwtPrincipal,
            @RequestPart("image") @NotNull MultipartFile image
    ) {
        return ResponseEntity.ok(redotMemberService.uploadProfileImage(jwtPrincipal.id(), image));
    }

    @DeleteMapping
    @Override
    public ResponseEntity<Void> deleteRedotMember(
            @AuthenticationPrincipal JwtPrincipal jwtPrincipal
    ) {
        redotMemberService.deleteRedotMember(jwtPrincipal.id());
        return ResponseEntity.noContent().build();
    }
}
