package redot.redot_server.domain.cms.inquiry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import redot.redot_server.domain.redot.app.entity.RedotApp;
import redot.redot_server.domain.cms.inquiry.exception.RedotAppInquiryErrorCode;
import redot.redot_server.domain.cms.inquiry.exception.RedotAppInquiryException;
import redot.redot_server.domain.cms.member.entity.CMSMember;
import redot.redot_server.global.common.entity.BaseTimeEntity;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "redot_app_inquiries")
public class RedotAppInquiry extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redot_app_id")
    private RedotApp redotApp;

    @Column(nullable = false, unique = true)
    private String inquiryNumber;

    @Column(nullable = false)
    private String inquirerName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RedotAppInquiryStatus status;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private CMSMember assignee;

    public static RedotAppInquiry create(RedotApp redotApp, String inquiryNumber, String inquirerName, String title,
                                         String content) {
        return RedotAppInquiry.builder()
                .redotApp(redotApp)
                .inquiryNumber(inquiryNumber)
                .inquirerName(inquirerName)
                .title(title)
                .content(content)
                .status(RedotAppInquiryStatus.UNPROCESSED)
                .build();
    }

    public void processInquiry(CMSMember assignee) {
        if (this.status != RedotAppInquiryStatus.UNPROCESSED) {
            throw new RedotAppInquiryException(RedotAppInquiryErrorCode.REDOT_APP_INQUIRY_ALREADY_PROCESSED);
        }
        this.assignee = assignee;
        this.status = RedotAppInquiryStatus.COMPLETED;
    }

    public void reopenInquiry() {
        if( this.status != RedotAppInquiryStatus.COMPLETED) {
            throw new RedotAppInquiryException(RedotAppInquiryErrorCode.REDOT_APP_INQUIRY_NOT_COMPLETED);
        }
        this.assignee = null;
        this.status = RedotAppInquiryStatus.UNPROCESSED;
    }
}
