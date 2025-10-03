package redot.redot_server.domain.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import redot.redot_server.domain.cms.entity.Customer;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Builder(access = lombok.AccessLevel.PRIVATE)
@Table(name = "domains")
@Getter
public class Domain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String domainName;

    @Setter
    private String customDomain;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", unique = true)
    private Customer customer;

    @Column(nullable = false)
    private Boolean reserved;

    public static Domain ofReserved(String domainName) {
        return Domain.builder()
                .domainName(domainName)
                .reserved(true)
                .build();
    }

    public static Domain ofCustomer(String domainName, Customer customer) {
        return Domain.builder()
                .domainName(domainName)
                .customer(customer)
                .reserved(false)
                .build();
    }

    @PrePersist
    @PreUpdate
    private void validateIntegrity(){
        if (Boolean.TRUE.equals(reserved)) {
            if (this.customer != null || this.customDomain != null) {
                throw new IllegalStateException("Reserved domain cannot have customer or custom domain");
            }
        } else {
            if (this.customer == null) {
                throw new IllegalStateException("Non-reserved domain must have a customer");
            }
        }
    }
}
