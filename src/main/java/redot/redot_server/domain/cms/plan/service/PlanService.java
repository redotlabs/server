package redot.redot_server.domain.cms.plan.service;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.cms.plan.dto.PlanResponse;
import redot.redot_server.domain.cms.plan.repository.PlanRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanService {

    private final PlanRepository planRepository;

    public List<PlanResponse> getAllPlans() {
        return planRepository.findAll().stream()
                .sorted(Comparator.comparing(plan -> plan.getPlanType().getLevel()))
                .map(PlanResponse::fromEntity)
                .toList();
    }
}
