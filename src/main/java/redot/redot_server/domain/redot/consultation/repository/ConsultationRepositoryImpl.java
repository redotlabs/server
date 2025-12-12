package redot.redot_server.domain.redot.consultation.repository;

import static redot.redot_server.domain.redot.consultation.entity.QConsultation.consultation;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import redot.redot_server.domain.admin.dto.ConsultationSearchCondition;
import redot.redot_server.domain.redot.consultation.entity.Consultation;
import redot.redot_server.domain.redot.consultation.entity.ConsultationStatus;
import redot.redot_server.domain.redot.consultation.entity.ConsultationType;

@Repository
@RequiredArgsConstructor
public class ConsultationRepositoryImpl implements ConsultationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Consultation> findAllBySearchCondition(ConsultationSearchCondition condition, Pageable pageable) {
        BooleanExpression[] predicates = new BooleanExpression[]{
                emailContains(condition.email()),
                phoneContains(condition.phone()),
                statusCondition(condition.status()),
                typeEq(condition.type()),
                currentWebsiteUrlContains(condition.currentWebsiteUrl()),
                createdAtGoe(condition.startDate()),
                createdAtLt(condition.endDate())
        };

        List<Consultation> content = queryFactory.selectFrom(consultation)
                .where(predicates)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortOrder(pageable))
                .fetch();

        Long totalCount = queryFactory.select(consultation.count())
                .from(consultation)
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
        return order.isAscending() ? consultation.createdAt.asc() : consultation.createdAt.desc();
    }

    private BooleanExpression emailContains(String email) {
        return hasText(email) ? consultation.email.containsIgnoreCase(email) : null;
    }

    private BooleanExpression phoneContains(String phone) {
        return hasText(phone) ? consultation.phone.containsIgnoreCase(phone) : null;
    }

    private BooleanExpression currentWebsiteUrlContains(String url) {
        return hasText(url) ? consultation.currentWebsiteUrl.containsIgnoreCase(url) : null;
    }

    private BooleanExpression statusCondition(ConsultationStatus status) {
        if (status == null) {
            return consultation.status.ne(ConsultationStatus.CANCELLED);
        }
        return consultation.status.eq(status);
    }

    private BooleanExpression typeEq(ConsultationType type) {
        return type == null ? null : consultation.type.eq(type);
    }

    private BooleanExpression createdAtGoe(LocalDate startDate) {
        return startDate == null ? null : consultation.createdAt.goe(startDate.atStartOfDay());
    }

    private BooleanExpression createdAtLt(LocalDate endDate) {
        return endDate == null ? null : consultation.createdAt.lt(endDate.plusDays(1).atStartOfDay());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
