package com.spammers.AlertsAndNotifications.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name="Automatic_Notifications")
public class AutomaticModel extends NotificationModel{

    @Column(name="loanId", nullable = false)
    private String loanId;
}
