package redot.redot_server.domain.redot.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.redot.member.dto.RedotMemberCreateRequest;
import redot.redot_server.domain.redot.member.dto.RedotMemberResponse;
import redot.redot_server.domain.redot.member.service.RedotMemberService;

@RestController
@RequestMapping("/api/v1/redot/members")
@RequiredArgsConstructor
public class RedotMemberController {

    private final RedotMemberService redotMemberService;

    @PostMapping("/sign-up")
    public ResponseEntity<RedotMemberResponse> signUpRedotMember(@RequestBody RedotMemberCreateRequest request) {
        return ResponseEntity.ok(redotMemberService.createRedotMember(request));
    }
}
