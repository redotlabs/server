package redot.redot_server.domain.redot.plan.entity;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlanType {
    FREE("무료 플랜", 0),
    PRO("프로 플랜", 1),
    ENTERPRISE("엔터프라이즈 플랜", 2);

    private final String displayName;
    private final int level;

    /**
     * 현재 플랜보다 낮거나 같은 레벨의 모든 플랜 타입을 반환
     * 예: BASIC.getIncludedPlans() -> [FREE, BASIC]
     */
    public List<PlanType> getIncludedPlans() {
        return Arrays.stream(PlanType.values())
                .filter(plan -> plan.level <= this.level)
                .toList();
    }

    /**
     * 현재 플랜이 특정 플랜의 기능을 포함하는지 확인
     */
    public boolean includes(PlanType planType) {
        return this.level >= planType.level;
    }

    /**
     * 플랜 간 비교
     */
    public boolean isHigherThan(PlanType planType) {
        return this.level > planType.level;
    }

    public boolean isLowerThan(PlanType planType) {
        return this.level < planType.level;
    }
}

