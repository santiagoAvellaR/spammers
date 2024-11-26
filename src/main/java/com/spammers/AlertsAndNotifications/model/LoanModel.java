package com.spammers.AlertsAndNotifications.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Loans")
@RequiredArgsConstructor
@Getter
@Setter
public class LoanModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String loanId;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FineModel> fines = new ArrayList<>();

    @Column(name = "userId", nullable = false)
    private String userId;

    @Column(name = "bookId", nullable = false)
    private String bookId;

    @Column(name = "loanDate", nullable = false)
    private LocalDate loanDate;

    @Column(name="bookName", nullable = false)
    private String bookName;

    @Column(name = "loanExpired", nullable = false)
    private LocalDate loanExpired;

    @Column(name="status", nullable=false)
    private boolean status;

    @Column(name="bookReturned", nullable = false)
    private boolean bookReturned;

    public LoanModel(String userId, String bookId, LocalDate loanDate, String bookName,LocalDate loanExpired, boolean status) {
        this.userId = userId;
        this.bookId = bookId;
        this.loanDate = loanDate;
        this.loanExpired = loanExpired;
        this.status = status;
        this.bookName = bookName;
        this.bookReturned = false;
    }
    public boolean getStatus() {
        return status;
    }
}

