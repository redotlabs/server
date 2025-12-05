package redot.redot_server.domain.site.domain.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.site.domain.dto.request.SubdomainLookupRequest;
import redot.redot_server.domain.site.domain.dto.response.SubdomainLookupResponse;
import redot.redot_server.domain.site.domain.entity.Domain;
import redot.redot_server.domain.site.domain.exception.DomainErrorCode;
import redot.redot_server.domain.site.domain.exception.DomainException;
import redot.redot_server.domain.site.domain.repository.DomainRepository;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DomainService {

    private final DomainRepository domainRepository;

    public SubdomainLookupResponse getSubdomain(SubdomainLookupRequest request) {
        Optional<Domain> customDomain = domainRepository.findByCustomDomain(request.customDomain());
        Domain domain = customDomain.orElseThrow(() -> new DomainException(DomainErrorCode.CUSTOM_DOMAIN_NOT_FOUND));

        return new SubdomainLookupResponse(domain.getSubdomain());
    }
}
