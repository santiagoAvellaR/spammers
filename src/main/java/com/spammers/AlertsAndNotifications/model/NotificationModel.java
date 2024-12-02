package com.spammers.AlertsAndNotifications.model;

import com.spammers.AlertsAndNotifications.model.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="Notifications")
@RequiredArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
public class NotificationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idNotification;

    @Column(name="studentId", nullable = false)
    private String studentId;

    @Column(name="emailGuardian", nullable = false)
    private String emailGuardian;

    @Column(name="sentDate", nullable = true)
    private LocalDate sentDate;

    @Column(name="type", nullable = false)
    private NotificationType notificationType;

    @Column(name = "hasBeenSeen", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean hasBeenSeen;

    @Column(name = "bookName", nullable = false)
    private String bookName;

    public NotificationModel(String studentId, String emailGuardian, LocalDate sendDate, NotificationType type, boolean hasBeenSeen, String bookName) {
        this.studentId = studentId;
        this.emailGuardian = emailGuardian;
        this.sentDate = sendDate;
        this.notificationType = type;
        this.hasBeenSeen = hasBeenSeen;
        this.bookName = bookName;
    }

}
