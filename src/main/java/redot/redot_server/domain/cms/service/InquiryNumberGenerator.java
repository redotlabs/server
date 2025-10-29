package redot.redot_server.domain.cms.service;

import jakarta.persistence.PessimisticLockException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redot.redot_server.domain.cms.entity.InquirySequence;
import redot.redot_server.domain.cms.exception.CustomerInquiryErrorCode;
import redot.redot_server.domain.cms.exception.CustomerInquiryException;
import redot.redot_server.domain.cms.repository.InquirySequenceRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryNumberGenerator {

    private final InquirySequenceRepository inquirySequenceRepository;

    @Transactional
    public String generateInquiryNumber() {
        LocalDate today = LocalDate.now();

        InquirySequence inquirySequence = inquirySequenceRepository.findByInquiryDateForUpdate(today)
                .orElseGet(() -> createSequenceSafely(today));
        long nextSeq = inquirySequence.getLastSequenceNumber() + 1;

        if (nextSeq > 99_999) {
            throw new CustomerInquiryException(CustomerInquiryErrorCode.INQUIRY_NUMBER_EXHAUSTED);
        }

        inquirySequence.updateLastSequenceNumber(nextSeq);

        return String.format("INQ%s%05d",
                today.format(DateTimeFormatter.BASIC_ISO_DATE),
                inquirySequence.getLastSequenceNumber()
        );
    }

    private InquirySequence createSequenceSafely(LocalDate inquiryDate) {
        try {
            return inquirySequenceRepository.save(InquirySequence.create(inquiryDate));
        } catch (DataIntegrityViolationException | PessimisticLockingFailureException | PessimisticLockException ex) {
            return inquirySequenceRepository.findByInquiryDateForUpdate(inquiryDate)
                    .orElseThrow(() -> ex);
        }
    }
}
