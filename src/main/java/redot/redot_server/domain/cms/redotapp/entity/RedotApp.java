package redot.redot_server.domain.cms.redotapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import redot.redot_server.domain.redot.member.entity.RedotMember;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redot_member_id")
    private RedotMember owner;

    @Column(nullable = false)
    private RedotAppStatus status;

    @Column(nullable = false)
    private String companyName;

    @CreatedDate
    private LocalDateTime createdAt;

    public static RedotApp create(String companyName, RedotMember owner) {
        return RedotApp.builder()
                .companyName(companyName)
                .owner(owner)
                .status(RedotAppStatus.ACTIVE)
                .build();
    }

    public static RedotApp createWithoutOwner(String companyName) {
        return RedotApp.builder()
                .companyName(companyName)
                .status(RedotAppStatus.ACTIVE)
                .build();
    }

}
