package com.spammers.AlertsAndNotifications.service.interfaces;

import com.spammers.AlertsAndNotifications.model.LoanModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<LoanModel, String> {
    /*
     * Optional<LoanModel> findByLoanId(String loanId);
    List<LoanModel> findLoanByBookId(String bookId);
    List<LoanModel> findLoanByBookIdAndUserId(String bookId, String loanId);
    List<LoanModel> findByLoanExpiredAfter(LocalDateTime currentDateTime);
    @Query("SELECT l FROM LoanModel l WHERE l.loanExpired < :currentDate")
    List<LoanModel> findExpiredLoans(@Param("currentDate") LocalDateTime currentDate);
     */



}
