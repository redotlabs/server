package redot.redot_server.domain.site.domain.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import redot.redot_server.domain.site.domain.dto.request.SubdomainLookupRequest;
import redot.redot_server.domain.site.domain.dto.response.SubdomainLookupResponse;

@Tag(name = "Domain", description = "도메인 API")
public interface DomainControllerDocs {

    @Operation(summary = "서브도메인 조회", description = "사용 가능한 서브도메인인지 확인합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = SubdomainLookupResponse.class)))
    ResponseEntity<SubdomainLookupResponse> getSubdomain(@Valid SubdomainLookupRequest request);
}
