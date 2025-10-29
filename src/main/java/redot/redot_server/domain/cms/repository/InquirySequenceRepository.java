package redot.redot_server.domain.cms.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import redot.redot_server.domain.cms.entity.InquirySequence;

public interface InquirySequenceRepository extends JpaRepository<InquirySequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-1")
    })
    @Query("select s from InquirySequence s where s.inquiryDate = :inquiryDate")
    Optional<InquirySequence> findByInquiryDateForUpdate(@Param("inquiryDate") LocalDate inquiryDate);
}
