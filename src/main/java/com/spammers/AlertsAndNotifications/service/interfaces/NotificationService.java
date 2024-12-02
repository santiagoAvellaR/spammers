package com.spammers.AlertsAndNotifications.service.interfaces;

import com.spammers.AlertsAndNotifications.model.dto.LoanDTO;
import com.spammers.AlertsAndNotifications.model.*;
import com.spammers.AlertsAndNotifications.model.dto.NotificationDTO;
import com.spammers.AlertsAndNotifications.model.dto.PaginatedResponseDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    void notifyLoan(LoanDTO loanDTO);
    PaginatedResponseDTO<FineOutputDTO> getFinesByUserId(String userId, int page, int size);
    PaginatedResponseDTO<NotificationDTO> getNotifications(String userId, int page, int size);
    void returnBook(String bookId, boolean returnedInBadCondition);
    void openFine(FineInputDTO fineInputDTO);
    void closeFine(String loanId);
}
