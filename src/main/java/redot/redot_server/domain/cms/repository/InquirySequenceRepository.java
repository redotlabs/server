package redot.redot_server.domain.cms.repository;

import java.time.LocalDate;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import redot.redot_server.domain.cms.entity.InquirySequence;

public interface InquirySequenceRepository extends JpaRepository<InquirySequence, Long> {

    Optional<InquirySequence> findByInquiryDate(LocalDate inquiryDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from InquirySequence s where s.inquiryDate = :inquiryDate")
    Optional<InquirySequence> findByInquiryDateForUpdate(@Param("inquiryDate") LocalDate inquiryDate);
}
