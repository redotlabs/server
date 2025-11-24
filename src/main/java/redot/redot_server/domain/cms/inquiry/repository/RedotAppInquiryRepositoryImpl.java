package redot.redot_server.domain.cms.inquiry.repository;

import static redot.redot_server.domain.cms.inquiry.entity.QRedotAppInquiry.redotAppInquiry;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import redot.redot_server.domain.cms.inquiry.dto.RedotAppInquirySearchCondition;
import redot.redot_server.domain.cms.inquiry.entity.RedotAppInquiry;
import redot.redot_server.domain.cms.inquiry.entity.RedotAppInquiryStatus;

@RequiredArgsConstructor
public class RedotAppInquiryRepositoryImpl implements RedotAppInquiryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<RedotAppInquiry> findAllBySearchCondition(Long redotAppId,
                                                          RedotAppInquirySearchCondition condition,
                                                          Pageable pageable) {
        BooleanExpression[] predicates = {
                redotAppEq(redotAppId),
                statusEq(condition.status()),
                inquiryNumberContains(condition.inquiryNumber()),
                titleContains(condition.title()),
                inquirerNameContains(condition.inquirerName()),
                createdAtGoe(condition.startDate()),
                createdAtLoe(condition.endDate())
        };

        List<RedotAppInquiry> content = queryFactory
                .selectFrom(redotAppInquiry)
                .where(predicates)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortOrder(pageable))
                .fetch();

        Long totalCount = queryFactory
                .select(redotAppInquiry.count())
                .from(redotAppInquiry)
                .where(predicates)
                .fetchOne();

        long total = totalCount == null ? 0L : totalCount;

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression redotAppEq(Long redotAppId) {
        return redotAppId == null ? null : redotAppInquiry.redotApp.id.eq(redotAppId);
    }

    private BooleanExpression statusEq(RedotAppInquiryStatus status) {
        return status == null ? null : redotAppInquiry.status.eq(status);
    }

    private BooleanExpression inquiryNumberContains(String keyword) {
        return hasText(keyword) ? redotAppInquiry.inquiryNumber.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression titleContains(String keyword) {
        return hasText(keyword) ? redotAppInquiry.title.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression inquirerNameContains(String keyword) {
        return hasText(keyword) ? redotAppInquiry.inquirerName.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression createdAtGoe(LocalDate from) {
        return (from == null) ? null : redotAppInquiry.createdAt.goe(from.atStartOfDay());
    }

    private BooleanExpression createdAtLoe(LocalDate to) {
        return (to == null) ? null : redotAppInquiry.createdAt.loe(to.atTime(LocalTime.MAX));
    }

    private OrderSpecifier<?> sortOrder(Pageable pageable) {
        Sort.Order order = pageable.getSort().stream()
                .filter(o -> o.getProperty().equals("createdAt"))
                .findFirst()
                .orElseGet(() -> new Sort.Order(Sort.Direction.DESC, "createdAt"));

        return order.isAscending() ? redotAppInquiry.createdAt.asc() : redotAppInquiry.createdAt.desc();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

}
