package com.spammers.AlertsAndNotifications.repository;

import com.spammers.AlertsAndNotifications.model.LoanModel;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<LoanModel, String> {
    @Query("SELECT l FROM LoanModel l WHERE l.loanExpired < :currentDate AND l.status=true")
    List<LoanModel> findExpiredLoans(@Param("currentDate") LocalDate currentDate, Pageable pageable);

    @Query("SELECT l FROM LoanModel l WHERE FUNCTION('DATE', l.loanExpired) = :dateInThreeDays")
    List<LoanModel> findLoansExpiringInExactlyNDays(
            @Param("dateInThreeDays") LocalDate dateInThreeDays,
            Pageable pageable);
}
