package redot.redot_server.domain.cms.plan.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import redot.redot_server.domain.cms.plan.entity.Plan;
import redot.redot_server.domain.cms.plan.entity.PlanType;

public record PlanResponse(
        Long id,
        PlanType planType,
        String displayName,
        String description,
        BigDecimal price,
        Long maxPageViews,
        Integer maxPages,
        Integer maxManagers,
        LocalDateTime createdAt
) {
    public static PlanResponse fromEntity(Plan plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getPlanType(),
                plan.getDisplayName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getMaxPageViews(),
                plan.getMaxPages(),
                plan.getMaxManagers(),
                plan.getCreatedAt()
        );
    }
}

