package redot.redot_server.domain.redot.member.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.SQLRestriction;
import redot.redot_server.global.common.entity.BaseTimeEntity;
import redot.redot_server.domain.redot.app.entity.RedotApp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(
        name = "redot_members",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"social_provider", "social_provider_id"})
        }
)
@SQLRestriction("status <> 'DELETED'")
public class RedotMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(nullable = false)
    private String name;

    private String profileImageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RedotMemberStatus status;

    @Enumerated(EnumType.STRING)
    private SocialProvider socialProvider;

    private String socialProviderId;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "owner")
    @Builder.Default
    private List<RedotApp> ownedApps = new ArrayList<>();

    public static RedotMember create(
            String name,
            String email,
            String password,
            String profileImageUrl
            ) {
        return RedotMember.builder()
                .name(name)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .password(password)
                .status(RedotMemberStatus.ACTIVE)
                .build();
    }

    public static RedotMember createSocialMember(
            String name,
            String email,
            String profileImageUrl,
            SocialProvider socialProvider,
            String socialProviderId
    ) {
        return RedotMember.builder()
                .name(name)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .socialProvider(socialProvider)
                .socialProviderId(socialProviderId)
                .status(RedotMemberStatus.ACTIVE)
                .build();
    }

    public void delete(){
        this.status = RedotMemberStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public void changeStatus(RedotMemberStatus status) {
        this.status = status;
        if (status != RedotMemberStatus.DELETED) {
            this.deletedAt = null;
        }
    }

    public void linkSocialAccount(SocialProvider socialProvider,
                                  String socialProviderId,
                                  String name,
                                  String profileImageUrl) {
        this.socialProvider = socialProvider;
        this.socialProviderId = socialProviderId;
        if (name != null) {
            this.name = name;
        }
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    public void resetPassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateInfo(String name, String profileImageUrl) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }
}
