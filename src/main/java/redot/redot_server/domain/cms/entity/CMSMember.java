package redot.redot_server.domain.cms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Builder(access = AccessLevel.PRIVATE)
@Table(
        name = "cms_members",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"customer_id", "email"})
        }
)
public class CMSMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String profileImageUrl;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CMSMemberRole role;

    @CreatedDate
    private LocalDateTime createdAt;

    public boolean belongsTo(Customer customer) {
        return this.customer.equals(customer);
    }

    public static CMSMember create(Customer customer, String name, String email, String profileImageUrl,
                                   String password, CMSMemberRole role) {
        return CMSMember.builder()
                .customer(customer)
                .name(name)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .password(password)
                .role(role)
                .build();
    }
}

