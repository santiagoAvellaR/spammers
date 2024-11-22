package com.spammers.AlertsAndNotifications.service.interfaces;

import com.spammers.AlertsAndNotifications.model.FineModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FinesRepository extends JpaRepository<FineModel, String> {
    @Query("SELECT l FROM FineModel l WHERE l.expiredDate = :currentDate")
    List<FineModel> findByExpiredDate(@Param("currentDate") LocalDateTime currentDate);

}
