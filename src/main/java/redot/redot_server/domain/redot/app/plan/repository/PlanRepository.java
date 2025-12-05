package redot.redot_server.domain.redot.app.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.redot.app.plan.entity.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {

}

