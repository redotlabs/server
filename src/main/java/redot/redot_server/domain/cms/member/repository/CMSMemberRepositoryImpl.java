package redot.redot_server.domain.cms.member.repository;

import static redot.redot_server.domain.cms.member.entity.QCMSMember.cMSMember;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import redot.redot_server.domain.cms.member.dto.request.CMSMemberSearchCondition;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.domain.cms.member.entity.CMSMemberRole;

@RequiredArgsConstructor
public class CMSMemberRepositoryImpl implements CMSMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CMSMember> findAllBySearchCondition(Long redotAppId, CMSMemberSearchCondition searchCondition,
                                                    Pageable pageable) {

        BooleanExpression[] predicates = {
                redotAppEq(redotAppId),
                nameContains(searchCondition.name()),
                emailContains(searchCondition.email()),
                roleEq(searchCondition.role())
        };

        List<CMSMember> content = queryFactory
                .selectFrom(cMSMember)
                .where(predicates)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortOrder(pageable))
                .fetch();

        Long totalCount = queryFactory
                .select(cMSMember.count())
                .from(cMSMember)
                .where(predicates)
                .fetchOne();

        long total = totalCount == null ? 0L : totalCount;

        return new PageImpl<>(content, pageable, total);
    }

    private OrderSpecifier<?> sortOrder(Pageable pageable) {
        Sort.Order order = pageable.getSort().stream()
                .filter(o -> o.getProperty().equals("createdAt"))
                .findFirst()
                .orElseGet(() -> new Sort.Order(Sort.Direction.DESC, "createdAt"));

        return order.isAscending() ? cMSMember.createdAt.asc() : cMSMember.createdAt.desc();
    }

    private BooleanExpression redotAppEq(Long redotAppId) {
        return redotAppId == null ? null : cMSMember.redotApp.id.eq(redotAppId);
    }

    private BooleanExpression nameContains(String name) {
        return hasText(name) ? cMSMember.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression emailContains(String email) {
        return hasText(email) ? cMSMember.email.containsIgnoreCase(email) : null;
    }

    private BooleanExpression roleEq(CMSMemberRole role) {
        return role == null ? null : cMSMember.role.eq(role);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
