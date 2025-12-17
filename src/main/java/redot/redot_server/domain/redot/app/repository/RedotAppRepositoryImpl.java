package redot.redot_server.domain.redot.app.repository;

import static redot.redot_server.domain.redot.app.entity.QRedotApp.redotApp;
import static redot.redot_server.domain.site.domain.entity.QDomain.domain;
import static redot.redot_server.domain.site.setting.entity.QSiteSetting.siteSetting;
import static redot.redot_server.domain.site.style.entity.QStyleInfo.styleInfo;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import redot.redot_server.domain.admin.dto.request.RedotAppInfoSearchCondition;
import redot.redot_server.domain.redot.app.entity.RedotApp;
import redot.redot_server.domain.redot.app.entity.RedotAppStatus;

@RequiredArgsConstructor
public class RedotAppRepositoryImpl implements RedotAppRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<RedotApp> findAllBySearchCondition(RedotAppInfoSearchCondition searchCondition,
                                                   Pageable pageable) {
        String name = searchCondition == null ? null : searchCondition.name();
        Long ownerId = searchCondition == null ? null : searchCondition.redotMemberId();
        RedotAppStatus status = searchCondition == null ? null : searchCondition.status();

        BooleanExpression[] predicates = new BooleanExpression[]{
                nameContains(name),
                ownerIdEq(ownerId),
                statusEq(status)
        };

        List<RedotApp> content = queryFactory
                .selectFrom(redotApp)
                .where(predicates)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(resolveOrder(pageable))
                .fetch();

        long total = countByPredicates(predicates);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<RedotAppWithSiteInfo> findAllWithSiteInfo(RedotAppInfoSearchCondition searchCondition,
                                                          Pageable pageable) {
        String name = searchCondition == null ? null : searchCondition.name();
        Long ownerId = searchCondition == null ? null : searchCondition.redotMemberId();
        RedotAppStatus status = searchCondition == null ? null : searchCondition.status();

        BooleanExpression[] predicates = new BooleanExpression[]{
                nameContains(name),
                ownerIdEq(ownerId),
                statusEq(status)
        };

        List<Tuple> tuples = queryFactory
                .select(redotApp, domain, siteSetting, styleInfo)
                .from(redotApp)
                .leftJoin(domain).on(domain.redotApp.eq(redotApp))
                .leftJoin(siteSetting).on(siteSetting.redotApp.eq(redotApp))
                .leftJoin(styleInfo).on(styleInfo.redotApp.eq(redotApp))
                .where(predicates)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(resolveOrder(pageable))
                .fetch();

        return tuples.stream()
                .map(tuple -> new RedotAppWithSiteInfo(
                        tuple.get(redotApp),
                        tuple.get(domain),
                        tuple.get(siteSetting),
                        tuple.get(styleInfo)
                ))
                .filter(appWithInfo -> appWithInfo.redotApp() != null)
                .toList();
    }

    @Override
    public long countBySearchCondition(RedotAppInfoSearchCondition searchCondition) {
        String name = searchCondition == null ? null : searchCondition.name();
        Long ownerId = searchCondition == null ? null : searchCondition.redotMemberId();
        RedotAppStatus status = searchCondition == null ? null : searchCondition.status();

        BooleanExpression[] predicates = new BooleanExpression[]{
                nameContains(name),
                ownerIdEq(ownerId),
                statusEq(status)
        };

        return countByPredicates(predicates);
    }

    private long countByPredicates(BooleanExpression[] predicates) {
        Long totalCount = queryFactory
                .select(redotApp.count())
                .from(redotApp)
                .where(predicates)
                .fetchOne();

        return totalCount == null ? 0L : totalCount;
    }

    private OrderSpecifier<?> resolveOrder(Pageable pageable) {
        Sort.Order order = pageable.getSort().stream()
                .filter(it -> Objects.equals(it.getProperty(), "createdAt"))
                .findFirst()
                .orElseGet(() -> new Sort.Order(Sort.Direction.DESC, "createdAt"));

        return order.isAscending() ? redotApp.createdAt.asc() : redotApp.createdAt.desc();
    }

    private BooleanExpression nameContains(String name) {
        return hasText(name) ? redotApp.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression ownerIdEq(Long ownerId) {
        return ownerId == null ? null : redotApp.owner.id.eq(ownerId);
    }

    private BooleanExpression statusEq(RedotAppStatus status) {
        return status == null ? null : redotApp.status.eq(status);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
