package redot.redot_server.domain.site.page.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "app_pages")
public class AppPage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "redot_app_id", nullable = false)
    private RedotApp redotApp;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 255)
    private String path;

    @Column(name = "is_protected", nullable = false)
    private Boolean isProtected;

    private String description;

    @Column(nullable = false, length = 100)
    private String title;

    public static AppPage create(RedotApp redotApp,
                                 String content,
                                 String path,
                                 Boolean isProtected,
                                 String description,
                                 String title) {
        return AppPage.builder()
                .redotApp(redotApp)
                .content(content)
                .path(path)
                .isProtected(isProtected)
                .description(description)
                .title(title)
                .build();
    }
}
