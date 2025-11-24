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
import redot.redot_server.domain.admin.exception.DomainErrorCode;
import redot.redot_server.domain.admin.exception.DomainException;
import redot.redot_server.domain.cms.redotapp.entity.RedotApp;

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
    private String subdomain;

    @Setter
    @Column(unique = true)
    private String customDomain;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redot_app_id", unique = true)
    private RedotApp redotApp;

    @Column(nullable = false)
    private Boolean reserved;

    public static Domain ofReserved(String subdomain) {
        return Domain.builder()
                .subdomain(subdomain)
                .reserved(true)
                .build();
    }

    public static Domain ofRedotApp(String subdomain, RedotApp redotApp) {
        return Domain.builder()
                .subdomain(subdomain)
                .redotApp(redotApp)
                .reserved(false)
                .build();
    }

    @PrePersist
    @PreUpdate
    private void validateIntegrity() {
        if (Boolean.TRUE.equals(reserved)) {
            if (this.redotApp != null || this.customDomain != null) {
                throw new DomainException(DomainErrorCode.RESERVED_DOMAIN_WITH_REDOT_APP);
            }
        } else {
            if (this.redotApp == null) {
                throw new DomainException(DomainErrorCode.NON_RESERVED_DOMAIN_MISSING_REDOT_APP);
            }
        }
    }

    public void updateCustomDomain(String customDomain) {
        this.customDomain = customDomain;
    }
}
