package redot.redot_server.domain.redot.app.plan.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.redot.app.plan.dto.response.PlanResponse;
import redot.redot_server.domain.redot.app.plan.service.PlanService;

@Tag(name = "Plan", description = "플랜 관리 API")
@RestController
@RequestMapping("/api/v1/app/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @GetMapping
    public List<PlanResponse> getAllPlans() {
        return planService.getAllPlans();
    }
}
