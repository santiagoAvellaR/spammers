package com.spammers.AlertsAndNotifications.service.interfaces;

import com.spammers.AlertsAndNotifications.model.dto.*;

public interface NotificationService {
    void notifyLoan(LoanDTO loanDTO);
    PaginatedResponseDTO<FineOutputDTO> getFinesByUserId(String userId, int page, int size);
    PaginatedResponseDTO<NotificationDTO> getNotifications(String userId, int page, int size);
    void returnBook(String bookId, boolean returnedInBadCondition);
    void openFine(FineInputDTO fineInputDTO);
    void closeFine(String loanId);
    UserNotificationsInformationDTO getNumberNotificationsNotSeenByUser(String userId);
    int markNotificationAsSeen(String notificationId);
}
