package redot.redot_server.domain.admin.dto;

import java.time.LocalDateTime;

public record AdminCustomerResponse(
        Long id,
        String customerId,
        String schemaName,
        String domainName,
        String customerName,
        String ownerEmail,
        String status,
        LocalDateTime createdAt
) {}
