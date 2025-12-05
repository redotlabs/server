package redot.redot_server.domain.redot.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import redot.redot_server.domain.redot.plan.entity.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {

}

