package redot.redot_server.domain.cms.menu.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.cms.menu.dto.response.CmsMenuResponse;
import redot.redot_server.domain.cms.menu.repository.CmsMenuRepository;
import redot.redot_server.domain.cms.plan.entity.Plan;
import redot.redot_server.domain.cms.plan.entity.PlanType;
import redot.redot_server.domain.cms.plan.exception.PlanErrorCode;
import redot.redot_server.domain.cms.plan.exception.PlanException;
import redot.redot_server.domain.cms.plan.repository.PlanRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CmsMenuService {

    private final CmsMenuRepository cmsMenuRepository;
    private final PlanRepository planRepository;

    /**
     * 특정 플랜의 메뉴 조회 (현재 플랜 + 하위 플랜 메뉴 포함)
     * 예: BASIC 플랜이면 FREE + BASIC 메뉴 모두 반환
     */
    public List<CmsMenuResponse> getMenusByPlanId(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanException(PlanErrorCode.PLAN_NOT_FOUND));

        // 현재 플랜보다 낮거나 같은 레벨의 모든 플랜 타입 가져오기
        List<PlanType> includedPlanTypes = plan.getPlanType().getIncludedPlans();
        
        return cmsMenuRepository.findByPlanTypesOrderByOrderAsc(includedPlanTypes).stream()
                .map(CmsMenuResponse::fromEntity)
                .toList();
    }
}
