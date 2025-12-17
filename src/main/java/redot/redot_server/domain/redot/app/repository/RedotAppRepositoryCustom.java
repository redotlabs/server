package redot.redot_server.domain.redot.app.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import redot.redot_server.domain.admin.dto.request.RedotAppInfoSearchCondition;
import redot.redot_server.domain.redot.app.entity.RedotApp;

public interface RedotAppRepositoryCustom {

    Page<RedotApp> findAllBySearchCondition(RedotAppInfoSearchCondition searchCondition,
                                            Pageable pageable);

    List<RedotAppWithSiteInfo> findAllWithSiteInfo(RedotAppInfoSearchCondition searchCondition,
                                                   Pageable pageable);

    long countBySearchCondition(RedotAppInfoSearchCondition searchCondition);
}
