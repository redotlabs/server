package redot.redot_server.domain.cms.inquiry.repository;

import static redot.redot_server.domain.cms.inquiry.entity.QCustomerInquiry.customerInquiry;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import redot.redot_server.domain.cms.inquiry.dto.CustomerInquirySearchCondition;
import redot.redot_server.domain.cms.inquiry.entity.CustomerInquiry;
import redot.redot_server.domain.cms.inquiry.entity.CustomerInquiryStatus;

@RequiredArgsConstructor
public class CustomerInquiryRepositoryImpl implements CustomInquiryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CustomerInquiry> findAllBySearchCondition(Long customerId,
                                                          CustomerInquirySearchCondition condition,
                                                          Pageable pageable) {
        JPAQuery<CustomerInquiry> contentQuery = queryFactory
                .selectFrom(customerInquiry)
                .where(
                        customerEq(customerId),
                        statusEq(condition.status()),
                        inquiryNumberContains(condition.inquiryNumber()),
                        titleContains(condition.title()),
                        inquirerNameContains(condition.inquirerName()),
                        createdAtGoe(condition.startDate()),
                        createdAtLoe(condition.endDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortOrder(pageable));

        List<CustomerInquiry> content = contentQuery.fetch();

        Long totalCount = queryFactory
                .select(customerInquiry.count())
                .from(customerInquiry)
                .where(
                        customerEq(customerId),
                        statusEq(condition.status()),
                        inquiryNumberContains(condition.inquiryNumber()),
                        titleContains(condition.title()),
                        inquirerNameContains(condition.inquirerName()),
                        createdAtGoe(condition.startDate()),
                        createdAtLoe(condition.endDate())
                )
                .fetchOne();

        long total = totalCount == null ? 0L : totalCount;

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression customerEq(Long customerId) {
        return customerInquiry.customer.id.eq(customerId);
    }

    private BooleanExpression statusEq(CustomerInquiryStatus status) {
        return status == null ? null : customerInquiry.status.eq(status);
    }

    private BooleanExpression inquiryNumberContains(String keyword) {
        return hasText(keyword) ? customerInquiry.inquiryNumber.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression titleContains(String keyword) {
        return hasText(keyword) ? customerInquiry.title.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression inquirerNameContains(String keyword) {
        return hasText(keyword) ? customerInquiry.inquirerName.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression createdAtGoe(LocalDate from) {
        return (from == null) ? null : customerInquiry.createdAt.goe(from.atStartOfDay());
    }

    private BooleanExpression createdAtLoe(LocalDate to) {
        return (to == null) ? null : customerInquiry.createdAt.loe(to.atTime(LocalTime.MAX));
    }

    private OrderSpecifier<?> sortOrder(Pageable pageable) {
        Sort.Order order = pageable.getSort().stream()
                .filter(o -> o.getProperty().equals("createdAt"))
                .findFirst()
                .orElseGet(() -> new Sort.Order(Sort.Direction.DESC, "createdAt"));

        return order.isAscending() ? customerInquiry.createdAt.asc() : customerInquiry.createdAt.desc();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

}
