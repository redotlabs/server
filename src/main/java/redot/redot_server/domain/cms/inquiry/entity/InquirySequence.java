package redot.redot_server.domain.cms.inquiry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import redot.redot_server.global.common.entity.BaseTimeEntity;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "inquiry_sequences")
public class InquirySequence extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long lastSequenceNumber;

    @Column(unique = true, nullable = false)
    private LocalDate inquiryDate;

    public static InquirySequence create(LocalDate inquiryDate) {
        return InquirySequence.builder()
                .lastSequenceNumber(0L)
                .inquiryDate(inquiryDate)
                .build();
    }

    public void updateLastSequenceNumber(Long nextSeq) {
        this.lastSequenceNumber = nextSeq;
    }
}
