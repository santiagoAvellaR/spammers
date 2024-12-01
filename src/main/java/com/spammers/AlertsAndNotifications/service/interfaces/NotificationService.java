package com.spammers.AlertsAndNotifications.service.interfaces;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.exceptions.SpammersPublicExceptions;
import com.spammers.AlertsAndNotifications.model.dto.LoanDTO;
import com.spammers.AlertsAndNotifications.model.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface NotificationService {
    void notifyLoan(LoanDTO loanDTO);
    void closeLoan(String bookId, String userId)throws SpammersPublicExceptions, SpammersPrivateExceptions ;
    List<FineModel> getFines(String userId);
    Map<String, Object> getNotifications(String userId, int page, int size);
    void returnBook(String bookId, boolean returnedInBadCondition);
    void openFine(FineInputDTO fineInputDTO);
    void closeFine(String loanId);
    Map<String, Object> returnAllActiveFines(int pageSize, int pageNumber);
    Map<String, Object> returnAllActiveFinesBetweenDate(LocalDate date, int pageSize, int pageNumber);
}
