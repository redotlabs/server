package redot.redot_server.domain.redot.member.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redot.redot_server.domain.redot.member.service.RedotMemberService;
import redot.redot_server.global.s3.dto.UploadedImageUrlResponse;
import redot.redot_server.global.security.principal.JwtPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/redot/members")
public class RedotMemberController {

    private final RedotMemberService redotMemberService;

    @PostMapping("/upload-profile-image")
    public ResponseEntity<UploadedImageUrlResponse> uploadProfileImage(
            @AuthenticationPrincipal JwtPrincipal jwtPrincipal,
            @RequestPart("image") @NotNull MultipartFile image
    ) {
        return ResponseEntity.ok(redotMemberService.uploadProfileImage(jwtPrincipal.id(), image));
    }
}
