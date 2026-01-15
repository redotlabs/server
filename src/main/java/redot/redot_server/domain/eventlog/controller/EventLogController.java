package redot.redot_server.domain.eventlog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.eventlog.controller.docs.EventLogControllerDocs;
import redot.redot_server.domain.eventlog.dto.PageViewCommand;
import redot.redot_server.domain.eventlog.dto.PageViewRequest;
import redot.redot_server.domain.eventlog.service.EventLogIngestService;
import redot.redot_server.global.redotapp.resolver.annotation.CurrentRedotApp;
import redot.redot_server.global.security.principal.JwtPrincipal;

import java.time.Instant;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/event-logs")
public class EventLogController implements EventLogControllerDocs {

    private final EventLogIngestService ingestService;

    /*
        페이지 뷰 이벤트 수집 엔드포인트
     */
    @PostMapping("/page-view")
    public ResponseEntity<Void> pageView(
            @CurrentRedotApp Long redotAppId,
            @Valid @RequestBody PageViewRequest req,
            HttpServletRequest servletRequest,
            @AuthenticationPrincipal JwtPrincipal jwtPrincipal // 없을 수도 있음(비회원)
    ) {
        String ip = extractClientIp(servletRequest);

        PageViewCommand cmd = new PageViewCommand(
                UUID.randomUUID(),
                redotAppId,
                req.deviceType(),
                ip,
                Instant.now()
        );

        ingestService.ingestPageView(cmd);
        return ResponseEntity.accepted().build();
    }

    /*
        클라이언트 IP 추출
     */
    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        String xri = request.getHeader("X-Real-IP");
        if (xri != null && !xri.isBlank()) return xri.trim();
        return request.getRemoteAddr();
    }
}