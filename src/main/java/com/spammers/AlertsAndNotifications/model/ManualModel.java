package com.spammers.AlertsAndNotifications.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="ManualNotifications")
public class ManualModel extends NotificationModel {
    @Column(name="repetitionPeriod", nullable = false)
    private int repetitionPeriod;
}
