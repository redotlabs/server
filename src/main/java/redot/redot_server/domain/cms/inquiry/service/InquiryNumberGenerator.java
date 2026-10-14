package redot.redot_server.domain.cms.inquiry.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.cms.inquiry.entity.InquirySequence;
import redot.redot_server.domain.cms.inquiry.exception.RedotAppInquiryErrorCode;
import redot.redot_server.domain.cms.inquiry.exception.RedotAppInquiryException;
import redot.redot_server.domain.cms.inquiry.repository.InquirySequenceRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryNumberGenerator {

    private final InquirySequenceRepository inquirySequenceRepository;
    private final Clock inquiryClock;

    @Transactional
    public String generateInquiryNumber() {
        LocalDate today = LocalDate.now(inquiryClock);

        inquirySequenceRepository.initializeDate(today);
        InquirySequence inquirySequence = inquirySequenceRepository.findByInquiryDateForUpdate(today)
                .orElseThrow(() -> new IllegalStateException("Inquiry sequence was not initialized"));
        long nextSeq = inquirySequence.getLastSequenceNumber() + 1;

        if (nextSeq > 99_999) {
            throw new RedotAppInquiryException(RedotAppInquiryErrorCode.INQUIRY_NUMBER_EXHAUSTED);
        }

        inquirySequence.updateLastSequenceNumber(nextSeq);

        return String.format("INQ%s%05d",
                today.format(DateTimeFormatter.BASIC_ISO_DATE),
                inquirySequence.getLastSequenceNumber()
        );
    }

}
