package redot.redot_server.domain.cms.customer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import redot.redot_server.domain.cms.customer.exception.CustomerErrorCode;
import redot.redot_server.domain.cms.customer.exception.CustomerException;
import redot.redot_server.domain.cms.member.entity.CMSMember;

@Getter
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Builder(access = lombok.AccessLevel.PRIVATE)
@Table(name = "customers")
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", unique = true)
    private CMSMember owner;

    @Column(nullable = false)
    private CustomerStatus status;

    @Column(nullable = false)
    private String companyName;

    @CreatedDate
    private LocalDateTime createdAt;

    public static Customer create(String companyName) {
        return Customer.builder()
                .companyName(companyName)
                .status(CustomerStatus.ACTIVE)
                .build();
    }

    public void setOwner(CMSMember owner) {
        if (this.owner != null) {
            throw new CustomerException(CustomerErrorCode.OWNER_ALREADY_ASSIGNED);
        }
        this.owner = owner;
    }
}
