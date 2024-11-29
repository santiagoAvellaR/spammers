package com.spammers.AlertsAndNotifications.model;



import com.spammers.AlertsAndNotifications.model.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@RequiredArgsConstructor
@Entity
@Table(name = "FineNotifications")
@Getter
@Setter
public class FineNotification extends NotificationModel {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fineId", nullable = false)
    private FineModel fine;

    public FineNotification(String studentId, String emailGuardian, LocalDate sendDate, NotificationType notificationType, FineModel fine) {
        super(studentId, emailGuardian, sendDate, notificationType);
        this.fine = fine;
    }
}
