package com.spammers.AlertsAndNotifications.service.interfaces;

import com.spammers.AlertsAndNotifications.model.LoanModel;
import com.spammers.AlertsAndNotifications.model.NotificationModel;

public interface NotificationService {
    void createNotification(LoanModel loan);
    void closeNotification(String idLoan);
    void createFine(LoanModel loan);
    void closeFine(String idLoan);
}
