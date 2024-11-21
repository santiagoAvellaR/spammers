package com.spammers.AlertsAndNotifications.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="Notifications")
@RequiredArgsConstructor
@Getter
public class NotificationModel {
    @Id
    private String idNotification;

    @Column(name="studentId", nullable = false)
    private String studentId;

    @Column(name="content", nullable = false, length = 300)
    private String content;

    @Column(name="sendDate", nullable = true)
    private LocalDateTime sendDate;
}
