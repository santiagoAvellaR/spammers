package com.spammers.AlertsAndNotifications.service.interfaces;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.exceptions.SpammersPublicExceptions;
import com.spammers.AlertsAndNotifications.model.*;
import com.spammers.AlertsAndNotifications.model.dto.LoanDTO;
import com.spammers.AlertsAndNotifications.model.dto.NotificationDTO;
import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface NotificationService {
    void notifyLoan(LoanDTO loanDTO);
    void closeLoan(String bookId, String userId)throws SpammersPublicExceptions, SpammersPrivateExceptions ;
    List<FineModel> getFines(String userId);
    Map<String, Object> getNotifications(String userId, int page, int size);
    void returnBook(String bookId, boolean returnedInBadCondition);
    void openFine(FineDTO fineDTO);
    void closeFine(String loanId);
}
