package com.spammers.AlertsAndNotifications.model;

import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="Fines")
@RequiredArgsConstructor
@Getter
public class FineModel {
    @Id
    private String fineId;

    @Column(name="loanId",nullable = false)
    private String loanId;

    @Column(name="description", nullable=false, length=300)
    private String description;

    @Column(name="amount", nullable = false)
    private float amount;

    @Column(name="expiredDate", nullable = false)
    private LocalDateTime expiredDate;

    @Column(name="fineStatus", nullable = false)
    private FineStatus fineStatus;
}
