package redot.redot_server.domain.redot.member.repository;

import static redot.redot_server.domain.redot.app.entity.QRedotApp.redotApp;
import static redot.redot_server.domain.redot.member.entity.QRedotMember.redotMember;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import redot.redot_server.domain.admin.dto.AdminRedotMemberSearchCondition;
import redot.redot_server.domain.admin.dto.response.AdminRedotMemberProjection;
import redot.redot_server.domain.admin.dto.response.AdminRedotMemberProjectionImpl;
import redot.redot_server.domain.redot.member.entity.RedotMemberStatus;
import redot.redot_server.domain.redot.member.entity.SocialProvider;

@Repository
@RequiredArgsConstructor
public class RedotMemberRepositoryImpl implements RedotMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminRedotMemberProjection> searchAdminMembers(AdminRedotMemberSearchCondition condition, Pageable pageable) {
        List<BooleanExpression> predicates = buildPredicates(condition);
        BooleanExpression[] filters = predicates.toArray(new BooleanExpression[0]);

        List<AdminRedotMemberProjectionImpl> fetched = queryFactory
                .select(Projections.constructor(AdminRedotMemberProjectionImpl.class,
                        redotMember.id,
                        redotMember.email,
                        redotMember.name,
                        redotMember.profileImageUrl,
                        redotMember.socialProvider,
                        redotMember.createdAt,
                        redotMember.status,
                        redotApp.id.count()))
                .from(redotMember)
                .leftJoin(redotApp).on(redotApp.owner.eq(redotMember))
                .where(filters)
                .groupBy(redotMember.id,
                        redotMember.email,
                        redotMember.name,
                        redotMember.profileImageUrl,
                        redotMember.socialProvider,
                        redotMember.createdAt,
                        redotMember.status)
                .orderBy(resolveSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<AdminRedotMemberProjection> content = new ArrayList<>(fetched);

        Long totalCount = queryFactory.select(redotMember.count())
                .from(redotMember)
                .where(filters)
                .fetchOne();

        long total = totalCount == null ? 0L : totalCount;

        return new PageImpl<>(content, pageable, total);
    }

    private List<BooleanExpression> buildPredicates(AdminRedotMemberSearchCondition condition) {
        List<BooleanExpression> predicates = new ArrayList<>();
        if (condition == null) {
            return predicates;
        }
        if (StringUtils.hasText(condition.email())) {
            predicates.add(redotMember.email.containsIgnoreCase(condition.email()));
        }
        if (StringUtils.hasText(condition.name())) {
            predicates.add(redotMember.name.containsIgnoreCase(condition.name()));
        }
        SocialProvider socialProvider = condition.socialProvider();
        if (socialProvider != null) {
            predicates.add(redotMember.socialProvider.eq(socialProvider));
        }
        RedotMemberStatus status = condition.status();
        if (status != null) {
            predicates.add(redotMember.status.eq(status));
        }
        return predicates;
    }

    private OrderSpecifier<?> resolveSort(Pageable pageable) {
        Sort.Order createdAtOrder = pageable.getSort().stream()
                .filter(order -> order.getProperty().equals("createdAt"))
                .findFirst()
                .orElseGet(() -> new Sort.Order(Sort.Direction.DESC, "createdAt"));
        return createdAtOrder.isAscending() ? redotMember.createdAt.asc() : redotMember.createdAt.desc();
    }
}
