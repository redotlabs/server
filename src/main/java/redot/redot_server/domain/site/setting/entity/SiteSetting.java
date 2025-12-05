package redot.redot_server.domain.site.setting.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import redot.redot_server.domain.redot.app.entity.RedotApp;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Builder(access = lombok.AccessLevel.PRIVATE)
@Table(name = "site_settings")
public class SiteSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redot_app_id", nullable = false, unique = true)
    private RedotApp redotApp;

    private String logoUrl;

    private String siteName;

    private String gaInfo;

    public static SiteSetting createDefault(RedotApp redotApp) {
        return SiteSetting.builder()
                .redotApp(redotApp)
                .build();
    }

    public void updateLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void updateSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void updateGaInfo(String gaInfo) {
        this.gaInfo = gaInfo;
    }
}
