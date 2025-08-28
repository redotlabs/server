package redot.redot_server.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.admin.dto.AdminCustomerResponse;
import redot.redot_server.domain.admin.dto.CustomerCreateRequest;
import redot.redot_server.domain.admin.entity.AdminCustomer;
import redot.redot_server.domain.admin.repository.AdminCustomerRepository;
import redot.redot_server.domain.admin.util.CustomerInfoNaming;

@Service
@RequiredArgsConstructor
public class AdminCustomerService {

    private final AdminCustomerRepository repository;
    private final CustomerDdlExecutor ddl;

    /**
     * 한 트랜잭션에서:
     *  1) 스키마 생성
     *  2) 테넌트 customer 테이블 생성
     *  3) 테넌트 customer row insert
     *  4) admin.customers insert (JPA)
     */
    @Transactional
    public AdminCustomerResponse create(CustomerCreateRequest req) {
        // 0) 식별자/네이밍 준비
        String customerId = CustomerInfoNaming.genUuid();
        String token8 = CustomerInfoNaming.genToken8();

        String schemaName = uniqueSchemaName(token8);
        String domainName = uniqueDomainName(token8);

        // 1,2,3) 스키마 생성, 테이블 생성, 데이터 추가
        ddl.createSchemaIfNotExists(schemaName);
        ddl.createTenantCustomerTableIfNotExists(schemaName);
        String theme = (req.theme() == null) ? "CLASSIC" : req.theme().name();
        ddl.insertTenantCustomerRow(schemaName, customerId, req.customerName(), theme);

        // 4) admin.customers 데이터 추가 (JPA)
        AdminCustomer adminCustomer = repository.save(
                AdminCustomer.create(
                        customerId,
                        req.customerName(),
                        schemaName,
                        domainName,
                        req.adminEmail()
                )
        );

        return new AdminCustomerResponse(
                adminCustomer.getId(),
                adminCustomer.getCustomerId(),
                adminCustomer.getSchemaName(),
                adminCustomer.getDomainName(),
                adminCustomer.getCustomerName(),
                adminCustomer.getOwnerEmail(),
                adminCustomer.getStatus().name(),
                adminCustomer.getCreatedAt()
        );
    }

    private String uniqueSchemaName(String token8) {
        String base = CustomerInfoNaming.toSchemaName(token8);
        String candidate = base;
        int i = 0;
        while (repository.existsBySchemaName(candidate)) {
            i++;
            candidate = base + "_" + i;
            if (candidate.length() > 63) {
                token8 = CustomerInfoNaming.genToken8();
                base = CustomerInfoNaming.toSchemaName(token8);
                candidate = base;
                i = 0;
            }
        }
        return candidate;
    }

    private String uniqueDomainName(String token8) {
        String base = CustomerInfoNaming.toDomain(token8);
        String candidate = base;
        int i = 0;
        while (repository.existsByDomainName(candidate)) {
            i++;
            candidate = token8 + i + base.substring(token8.length());
        }
        return candidate;
    }
}
