package redot.redot_server.domain.admin.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.dto.SubdomainLookupRequest;
import redot.redot_server.domain.admin.dto.SubdomainLookupResponse;
import redot.redot_server.domain.admin.entity.Domain;
import redot.redot_server.domain.admin.exception.DomainErrorCode;
import redot.redot_server.domain.admin.exception.DomainException;
import redot.redot_server.domain.admin.repository.DomainRepository;

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
