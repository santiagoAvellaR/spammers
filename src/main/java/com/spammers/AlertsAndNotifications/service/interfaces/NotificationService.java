package com.spammers.AlertsAndNotifications.service.interfaces;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.exceptions.SpammersPublicExceptions;
import com.spammers.AlertsAndNotifications.model.LoanDTO;
import com.spammers.AlertsAndNotifications.model.LoanModel;
import com.spammers.AlertsAndNotifications.model.enums.FineStatus;

import java.time.LocalDate;

public interface NotificationService {
    void createLoan(String idBook, LoanDTO loan, String email, float fineRate) throws SpammersPublicExceptions, SpammersPrivateExceptions;
    void closeLoan(String idLoan);
    // They don't create the Fines, we do.
    void openFine(String loanId, String description, float amount, String email);
    void closeFine(String loanId);
}
