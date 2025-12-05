package redot.redot_server.domain.cms.member.entity;

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
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import redot.redot_server.domain.redot.app.entity.RedotApp;
import redot.redot_server.domain.cms.member.exception.CMSMemberErrorCode;
import redot.redot_server.domain.cms.member.exception.CMSMemberException;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Builder(access = AccessLevel.PRIVATE)
@SQLRestriction("status = 'ACTIVE'")
@Table(
        name = "cms_members",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"redot_app_id", "email"})
        }
)
public class CMSMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redot_app_id", nullable = false)
    private RedotApp redotApp;

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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CMSMemberStatus status;

    private LocalDateTime deletedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    public boolean belongsTo(RedotApp redotApp) {
        return this.redotApp.equals(redotApp);
    }

    public static CMSMember create(RedotApp redotApp, String name, String email, String profileImageUrl,
                                   String password, CMSMemberRole role) {
        return CMSMember.builder()
                .redotApp(redotApp)
                .name(name)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .password(password)
                .role(role)
                .status(CMSMemberStatus.ACTIVE)
                .build();
    }

    public static CMSMember join(RedotApp redotApp, String name, String email, String password,
                                 CMSMemberRole role) {
        return CMSMember.builder()
                .redotApp(redotApp)
                .name(name)
                .email(email)
                .password(password)
                .role(role)
                .status(CMSMemberStatus.ACTIVE)
                .build();
    }

    public void changeRole(CMSMemberRole role) {
        if (this.role.equals(role)) {
            throw new CMSMemberException(CMSMemberErrorCode.CMS_MEMBER_ROLE_UNCHANGED);
        }
        this.role = role;
    }

    public void updateProfile(String name, String profileImageUrl) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public void delete() {
        if(this.status == CMSMemberStatus.DELETED) {
            throw new CMSMemberException(CMSMemberErrorCode.CMS_MEMBER_ALREADY_DELETED);
        }
        this.status = CMSMemberStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public void resetPassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
