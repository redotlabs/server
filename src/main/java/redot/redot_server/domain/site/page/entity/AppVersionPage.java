package redot.redot_server.domain.site.page.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import redot.redot_server.global.common.entity.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "app_version_pages", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"app_version_id", "app_page_id"})
})
public class AppVersionPage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "app_version_id", nullable = false)
    private AppVersion appVersion;

    @ManyToOne
    @JoinColumn(name = "app_page_id", nullable = false)
    private AppPage appPage;

    public static AppVersionPage create(AppVersion appVersion, AppPage appPage) {
        return new AppVersionPage(null, appVersion, appPage);
    }
}
