package redot.redot_server.domain.cms.redotapp.entity;

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
import redot.redot_server.domain.cms.redotapp.exception.RedotAppErrorCode;
import redot.redot_server.domain.cms.redotapp.exception.RedotAppException;
import redot.redot_server.domain.cms.member.entity.CMSMember;

@Getter
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Builder(access = lombok.AccessLevel.PRIVATE)
@Table(name = "redot_apps")
public class RedotApp {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", unique = true)
    private CMSMember owner;

    @Column(nullable = false)
    private RedotAppStatus status;

    @Column(nullable = false)
    private String companyName;

    @CreatedDate
    private LocalDateTime createdAt;

    public static RedotApp create(String companyName) {
        return RedotApp.builder()
                .companyName(companyName)
                .status(RedotAppStatus.ACTIVE)
                .build();
    }

    public void setOwner(CMSMember owner) {
        if (this.owner != null) {
            throw new RedotAppException(RedotAppErrorCode.OWNER_ALREADY_ASSIGNED);
        }
        this.owner = owner;
    }
}
