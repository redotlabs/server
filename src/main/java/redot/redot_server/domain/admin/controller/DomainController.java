package redot.redot_server.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.admin.dto.SubdomainLookupRequest;
import redot.redot_server.domain.admin.dto.SubdomainLookupResponse;
import redot.redot_server.domain.admin.service.DomainService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/domain")
public class DomainController {

    private final DomainService domainService;

    @PostMapping("/subdomain")
    public ResponseEntity<SubdomainLookupResponse> getSubdomain(@RequestBody SubdomainLookupRequest request) {
        return ResponseEntity.ok(domainService.getSubdomain(request));

    }
}
