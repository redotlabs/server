package redot.redot_server.domain.redot.plan.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.redot.plan.controller.docs.PlanControllerDocs;
import redot.redot_server.domain.redot.plan.dto.response.PlanResponse;
import redot.redot_server.domain.redot.plan.service.PlanService;

@RestController
@RequestMapping("/api/v1/app/plans")
@RequiredArgsConstructor
public class PlanController implements PlanControllerDocs {

    private final PlanService planService;

    @GetMapping
    @Override
    public List<PlanResponse> getAllPlans() {
        return planService.getAllPlans();
    }
}
