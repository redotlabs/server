package redot.redot_server.domain.site.menu.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import redot.redot_server.domain.site.menu.entity.CMSMenu;
import redot.redot_server.domain.redot.plan.entity.PlanType;

public interface CMSMenuRepository extends JpaRepository<CMSMenu, Long> {

    List<CMSMenu> findByPlanIdOrderByOrderAsc(Long planId);

    @Query("SELECT cm FROM CMSMenu cm WHERE cm.plan.planType IN :planTypes ORDER BY cm.order ASC")
    List<CMSMenu> findByPlanTypesOrderByOrderAsc(@Param("planTypes") List<PlanType> planTypes);
}

