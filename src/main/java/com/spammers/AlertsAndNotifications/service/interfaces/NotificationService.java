package com.spammers.AlertsAndNotifications.service.interfaces;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.exceptions.SpammersPublicExceptions;
import com.spammers.AlertsAndNotifications.model.FineModel;
import com.spammers.AlertsAndNotifications.model.LoanDTO;
import com.spammers.AlertsAndNotifications.model.LoanModel;
import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import java.time.LocalDate;
import java.util.List;

public interface NotificationService {
    void notifyLoan(LoanDTO loanDTO);
    void closeLoan(String bookId, String userId)throws SpammersPublicExceptions, SpammersPrivateExceptions ;
    List<FineModel> getFines(String userId);
    List<NotificationModel> getNotifications(String userId);
    void returnBook(String bookId, boolean returnedInBadCondition);
    void openFine(String loanId, String description, float amount, String email);
    void closeFine(String loanId);
}
