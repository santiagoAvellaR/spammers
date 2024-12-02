package com.spammers.AlertsAndNotifications.repository;

import com.spammers.AlertsAndNotifications.model.FineModel;
import com.spammers.AlertsAndNotifications.model.LoanModel;
import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import com.spammers.AlertsAndNotifications.model.enums.FineType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
/**
 * Repository interface for handling data access operations related to fines.
 * Extends JpaRepository to provide basic CRUD operations.
 */
public interface FinesRepository extends JpaRepository<FineModel, String> {

    /**
     * Finds all fines that have an expiration date matching the provided current date.
     *
     * @param currentDate The current date used to find fines that expire on this date.
     *                    The parameter should be of type {@link LocalDateTime}.
     * @return A list of {@link FineModel} objects that have the specified expiration date.
     *         If no fines are found, an empty list is returned.
     */
    @Query("SELECT l FROM FineModel l WHERE l.expiredDate = :currentDate")
    List<FineModel> findByExpiredDate(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Finds a fine by its unique identifier.
     *
     * @param id The unique identifier of the fine to search for.
     *           The parameter should be of type {@link String}.
     * @return An {@link Optional<FineModel>} containing the fine if found, or {@link Optional#empty()}
     *         if no fine with the given identifier is found.
     */
    Optional<FineModel> findById(String id);

    /**
     * Updates the status of a fine in the database.
     *
     * This method uses a JPQL query to update the status of a fine identified by its unique ID.
     * The update is performed directly in the database without loading the entity into the persistence context.
     *
     * @param fineId    The unique identifier of the fine to be updated.
     *                  The parameter should be of type {@link String}.
     * @param newStatus The new status to be assigned to the fine.
     *                  The parameter should be of type {@link String}.
     */
    @Modifying
    @Query("UPDATE FineModel f  SET f.fineStatus = :newStatus WHERE f.fineId = :fineId")
    void updateFineStatus(@Param("fineId") String fineId, @Param("newStatus") FineStatus newStatus);

    @Query("SELECT l FROM FineModel l WHERE l.fineType = :givenFineType")
    List<FineModel> findByFineType(@Param("givenFineType") FineType givenFineType);

    @Query("SELECT f FROM FineModel f WHERE f.loan.loanId = :givenLoanId")
    List<FineModel> findByLoanId(@Param("givenLoanId") String givenLoanId);

    @Query("SELECT f FROM FineModel f WHERE f.fineStatus = :givenFineStatus")
    Page<FineModel> findByStatus(@Param("givenFineStatus") FineStatus givenFineStatus, Pageable pageable);


    @Query(value = """
       SELECT * 
       FROM fines f 
       WHERE EXTRACT(YEAR FROM f.expired_date) = EXTRACT(YEAR FROM CAST(:givenDate AS DATE))
         AND EXTRACT(MONTH FROM f.expired_date) = EXTRACT(MONTH FROM CAST(:givenDate AS DATE))
         AND f.fine_status = :givenFineStatus
       ORDER BY f.expired_date DESC
       """,
            nativeQuery = true)
    Page<FineModel> findByStatusAndDate(
            @Param("givenFineStatus") FineStatus givenFineStatus,
            @Param("givenDate") LocalDate givenDate,
            Pageable pageable);

    @Query("SELECT f FROM FineModel f WHERE f.loan.userId = :givenUserId")
    Page<FineModel> findByUserId(@Param("givenUserId") String givenUserId, Pageable pageable);
}
