package redot.redot_server.domain.cms.inquiry.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import redot.redot_server.domain.cms.inquiry.entity.InquirySequence;

public interface InquirySequenceRepository extends JpaRepository<InquirySequence, Long> {

    // A competing first request waits for the inserting transaction, without aborting its own.
    @Modifying
    @Query(value = """
            INSERT INTO inquiry_sequences (inquiry_date, last_sequence_number)
            VALUES (:inquiryDate, 0)
            ON CONFLICT (inquiry_date) DO NOTHING
            """, nativeQuery = true)
    void initializeDate(@Param("inquiryDate") LocalDate inquiryDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-1")
    })
    @Query("select s from InquirySequence s where s.inquiryDate = :inquiryDate")
    Optional<InquirySequence> findByInquiryDateForUpdate(@Param("inquiryDate") LocalDate inquiryDate);
}
