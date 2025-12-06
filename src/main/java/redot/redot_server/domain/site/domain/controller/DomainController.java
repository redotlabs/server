package redot.redot_server.domain.site.domain.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.site.domain.dto.request.SubdomainLookupRequest;
import redot.redot_server.domain.site.domain.dto.response.SubdomainLookupResponse;
import redot.redot_server.domain.site.domain.service.DomainService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/domain")
public class DomainController {

    private final DomainService domainService;

    @PostMapping("/subdomain")
    public ResponseEntity<SubdomainLookupResponse> getSubdomain(@Valid @RequestBody SubdomainLookupRequest request) {
        return ResponseEntity.ok(domainService.getSubdomain(request));

    }
}
