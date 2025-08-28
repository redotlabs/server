package redot.redot_server.domain.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "customers", schema = "admin")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class AdminCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", unique = true, nullable = false, length = 64)
    private String customerId;

    @Column(name = "schema_name", unique = true, nullable = false, length = 63)
    private String schemaName;

    @Column(name = "domain_name", unique = true, length = 255)
    private String domainName;

    @Column(name = "customer_name", nullable = false, length = 255)
    private String customerName;

    @Column(name = "owner_email", nullable = false, length = 255)
    private String ownerEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static AdminCustomer create(String customerId,
                                       String customerName,
                                       String schemaName,
                                       String domainName,
                                       String ownerEmail) {
        return AdminCustomer.builder()
                .customerId(customerId)
                .customerName(customerName)
                .schemaName(schemaName)
                .domainName(domainName)
                .status(CustomerStatus.ACTIVE)
                .ownerEmail(ownerEmail)
                .build();
    }
}
