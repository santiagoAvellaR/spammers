package com.spammers.AlertsAndNotifications.service.interfaces;

import com.spammers.AlertsAndNotifications.model.dto.*;

public interface NotificationService {

    PaginatedResponseDTO<FineOutputDTO> getFinesByUserId(String userId, int page, int size);
    PaginatedResponseDTO<NotificationDTO> getNotifications(String userId, int page, int size);


}
