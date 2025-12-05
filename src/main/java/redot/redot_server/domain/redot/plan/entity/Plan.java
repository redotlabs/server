package redot.redot_server.domain.redot.plan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Builder(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "app_plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50, name = "plan_type")
    private PlanType planType;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Long maxPageViews;

    @Column(nullable = false)
    private Integer maxPages;

    @Column(nullable = false)
    private Integer maxManagers;

    @CreatedDate
    private LocalDateTime createdAt;

    public static Plan create(
            PlanType planType,
            String displayName,
            String description,
            BigDecimal price,
            Long maxPageViews,
            Integer maxPages,
            Integer maxManagers
    ) {
        return Plan.builder()
                .planType(planType)
                .displayName(displayName)
                .description(description)
                .price(price)
                .maxPageViews(maxPageViews)
                .maxPages(maxPages)
                .maxManagers(maxManagers)
                .build();
    }

    public void update(
            String displayName,
            String description,
            BigDecimal price,
            Long maxPageViews,
            Integer maxPages,
            Integer maxManagers
    ) {
        this.displayName = displayName;
        this.description = description;
        this.price = price;
        this.maxPageViews = maxPageViews;
        this.maxPages = maxPages;
        this.maxManagers = maxManagers;
    }
}
