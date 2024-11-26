package com.spammers.AlertsAndNotifications.repository;

import com.spammers.AlertsAndNotifications.model.NotificationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface NotificationRepository extends JpaRepository<NotificationModel, String> {
    @Query("SELECT l FROM NotificationModel l WHERE l.studentId = :userId")
    Page<NotificationModel> findByUserId(@Param("userId") String userId, Pageable pageable);
}
