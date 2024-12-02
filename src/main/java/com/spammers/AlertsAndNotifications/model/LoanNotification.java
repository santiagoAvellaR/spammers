package com.spammers.AlertsAndNotifications.model;


import com.spammers.AlertsAndNotifications.model.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "LoanNotifications")
@RequiredArgsConstructor
@Getter
@Setter
public class LoanNotification extends NotificationModel {
    @ManyToOne
    @JoinColumn(name = "loanId", nullable = false)
    private LoanModel loan;


    public LoanNotification(String studentId, String emailGuardian, LocalDate sentDate, NotificationType notificationType, LoanModel loan, boolean hasBeenSeen, String bookName) {
        super(studentId, emailGuardian, sentDate, notificationType, hasBeenSeen, bookName);
        this.loan = loan;
    }
}
