package com.spammers.AlertsAndNotifications.repository;

import com.spammers.AlertsAndNotifications.model.NotificationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationModel, String> {
}
