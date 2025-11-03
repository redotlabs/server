package redot.redot_server.domain.cms.dto;

import java.time.LocalDateTime;
import redot.redot_server.domain.cms.entity.Customer;
import redot.redot_server.domain.cms.entity.CustomerStatus;

public record CustomerResponse(
        Long id,
        String companyName,
        CustomerStatus status,
        LocalDateTime createdAt
) {
    public static CustomerResponse fromEntity(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getCompanyName(),
                customer.getStatus(),
                customer.getCreatedAt()
        );
    }
}
