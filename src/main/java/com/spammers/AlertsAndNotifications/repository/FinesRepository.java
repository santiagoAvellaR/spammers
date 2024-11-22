package com.spammers.AlertsAndNotifications.repository;

import com.spammers.AlertsAndNotifications.model.FineModel;
import com.spammers.AlertsAndNotifications.model.enums.FineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FinesRepository extends JpaRepository<FineModel, String> {
    @Query("SELECT l FROM FineModel l WHERE l.fineType = :givenFineType")
    List<FineModel> findByFineType(@Param("givenFineType") FineType givenFineType);

    @Query("SELECT l FROM FineModel l WHERE l.loan.loanId = :givenLoanId")
    Optional<FineModel> findByLoanId(@Param("givenLoanId") String givenLoanId);
}
