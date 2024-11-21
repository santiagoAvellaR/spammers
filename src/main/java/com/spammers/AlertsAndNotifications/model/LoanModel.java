package com.spammers.AlertsAndNotifications.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "Loans")
@RequiredArgsConstructor
@Getter
public class LoanModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String loanId;

    @Column(name = "userId", nullable = false)
    private String userId;

    @Column(name = "bookId", nullable = false)
    private String bookId;

    @Column(name = "loanDate", nullable = false)
    private LocalDate loanDate;

    @Column(name = "loanExpired", nullable = false)
    private LocalDate loanExpired;

    @Column(name="status", nullable=false)
    private boolean status;
    public LoanModel(String userId, String bookId, LocalDate loanDate, LocalDate loanExpired, boolean status) {
        this.userId = userId;
        this.bookId = bookId;
        this.loanDate = loanDate;
        this.loanExpired = loanExpired;
        this.status = status;
    }
}

