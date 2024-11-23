package com.spammers.AlertsAndNotifications.service.interfaces;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.exceptions.SpammersPublicExceptions;
import com.spammers.AlertsAndNotifications.model.LoanDTO;
import com.spammers.AlertsAndNotifications.model.LoanModel;

public interface NotificationService {
    void createLoan(String idBook, LoanDTO loan, String email, float fineRate) throws SpammersPublicExceptions, SpammersPrivateExceptions;
    void closeLoan(String idLoan)throws SpammersPublicExceptions, SpammersPrivateExceptions ;
    // They don't create the Fines, we do.
    //void createFine(LoanModel loan);
    void closeFine(String idLoan);
    void returnBook(LoanDTO loan, boolean returnedInBadCondition);
}
