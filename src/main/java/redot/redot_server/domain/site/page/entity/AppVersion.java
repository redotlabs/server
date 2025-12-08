package redot.redot_server.domain.site.page.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import redot.redot_server.domain.redot.app.entity.RedotApp;
import redot.redot_server.global.common.entity.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "app_versions")
public class AppVersion extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "redot_app_id", nullable = false)
    private RedotApp redotApp;

    private String remark;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppVersionStatus status;

    public static AppVersion create(RedotApp redotApp, AppVersionStatus status, String remark) {
        return AppVersion.builder()
                .redotApp(redotApp)
                .status(status)
                .remark(remark)
                .build();
    }

    public void changeStatus(AppVersionStatus status) {
        this.status = status;
    }
}
