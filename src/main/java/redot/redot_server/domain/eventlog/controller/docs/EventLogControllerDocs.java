package redot.redot_server.domain.eventlog.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import redot.redot_server.domain.eventlog.dto.PageViewRequest;
import redot.redot_server.global.security.principal.JwtPrincipal;

@Tag(name = "EventLogs", description = "이벤트 로그 관리 API")
public interface EventLogControllerDocs {
    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 Redot 앱의 서브도메인")
    @Operation(summary = "페이지 뷰 이벤트 수집",
            description = "클라이언트로부터 페이지 뷰 이벤트를 수집합니다.")
    @ApiResponse(responseCode = "202", description = "이벤트 수집 요청 접수 성공")
    ResponseEntity<Void> pageView(
            @Parameter(hidden = true) Long redotAppId,
            @Parameter(hidden = true) PageViewRequest req,
            @Parameter(hidden = true) HttpServletRequest servletRequest,
            @Parameter(hidden = true) JwtPrincipal jwtPrincipal
    );
}
