package com.spammers.AlertsAndNotifications.model;



import com.spammers.AlertsAndNotifications.model.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
@RequiredArgsConstructor
@Entity
@Table(name = "FineNotifications")
@Getter
@Setter
public class FineNotification extends NotificationModel {
    @ManyToOne
    @JoinColumn(name = "fineId", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private FineModel fine;

    public FineNotification(String studentId, String emailGuardian, LocalDate sendDate, NotificationType notificationType, FineModel fine, boolean hasBeenSeen, String bookName) {
        super(studentId, emailGuardian, sendDate, notificationType, hasBeenSeen, bookName);
        this.fine = fine;
    }
}
