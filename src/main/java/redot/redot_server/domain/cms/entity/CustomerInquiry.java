package redot.redot_server.domain.cms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import redot.redot_server.domain.cms.exception.CustomerInquiryErrorCode;
import redot.redot_server.domain.cms.exception.CustomerInquiryException;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "customer_inquiries")
@EntityListeners(AuditingEntityListener.class)

public class CustomerInquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = false, unique = true)
    private String inquiryNumber;

    @Column(nullable = false)
    private String inquirerName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomerInquiryStatus status;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private CMSMember assignee;

    @CreatedDate
    private LocalDateTime createdAt;

    public static CustomerInquiry create(Customer customer, String inquiryNumber, String inquirerName, String title,
                                         String content) {
        return CustomerInquiry.builder()
                .customer(customer)
                .inquiryNumber(inquiryNumber)
                .inquirerName(inquirerName)
                .title(title)
                .content(content)
                .status(CustomerInquiryStatus.UNPROCESSED)
                .build();
    }

    public void processInquiry(CMSMember assignee) {
        if (this.status != CustomerInquiryStatus.UNPROCESSED) {
            throw new CustomerInquiryException(CustomerInquiryErrorCode.CUSTOMER_INQUIRY_ALREADY_PROCESSED);
        }
        this.assignee = assignee;
        this.status = CustomerInquiryStatus.COMPLETED;
    }

    public void reopenInquiry() {
        if( this.status != CustomerInquiryStatus.COMPLETED) {
            throw new CustomerInquiryException(CustomerInquiryErrorCode.CUSTOMER_INQUIRY_NOT_COMPLETED);
        }
        this.assignee = null;
        this.status = CustomerInquiryStatus.UNPROCESSED;
    }
}
