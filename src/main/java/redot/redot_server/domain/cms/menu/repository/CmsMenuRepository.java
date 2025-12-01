package redot.redot_server.domain.cms.menu.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import redot.redot_server.domain.cms.menu.entity.CmsMenu;
import redot.redot_server.domain.cms.plan.entity.PlanType;

public interface CmsMenuRepository extends JpaRepository<CmsMenu, Long> {

    List<CmsMenu> findByPlanIdOrderByOrderAsc(Long planId);

    @Query("SELECT cm FROM CmsMenu cm WHERE cm.plan.planType IN :planTypes ORDER BY cm.order ASC")
    List<CmsMenu> findByPlanTypesOrderByOrderAsc(@Param("planTypes") List<PlanType> planTypes);
}

