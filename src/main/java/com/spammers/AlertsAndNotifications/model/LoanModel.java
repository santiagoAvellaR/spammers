package com.spammers.AlertsAndNotifications.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Loans")
@RequiredArgsConstructor
@Getter
public class LoanModel {
    @Id
    @Column(name = "idLoan", nullable = false, unique = true)
    private String loanId;

    @Column(name = "userId", nullable = false)
    private String userId;

    @Column(name = "bookId", nullable = false)
    private String bookId;

    @Column(name = "loanDate", nullable = false)
    private LocalDateTime loanDate;

    @Column(name = "loanExpired", nullable = false)
    private LocalDateTime loanExpired;
}

