package com.spammers.AlertsAndNotifications.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="Notifications")
@RequiredArgsConstructor
@Getter
public class NotificationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idNotification;

    @Column(name="studentId", nullable = false)
    private String studentId;

    @Column(name="emailGuardian", nullable = false)
    private String emailGuardian;

    @Column(name="sendDate", nullable = true)
    private LocalDate sendDate;

    public NotificationModel(String studentId, String emailGuardian, LocalDate sendDate) {
        this.studentId = studentId;
        this.emailGuardian = emailGuardian;
        this.sendDate = sendDate;
    }
}
