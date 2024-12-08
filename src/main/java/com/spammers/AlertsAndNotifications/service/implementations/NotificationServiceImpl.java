package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.model.dto.*;
import com.spammers.AlertsAndNotifications.model.NotificationModel;
import com.spammers.AlertsAndNotifications.model.*;
import com.spammers.AlertsAndNotifications.model.enums.*;
import com.spammers.AlertsAndNotifications.repository.FinesRepository;
import com.spammers.AlertsAndNotifications.repository.NotificationRepository;
import com.spammers.AlertsAndNotifications.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * This service provides the Notifications features, in order to handle the
 * possible notifications to the user's guardian and handle Fines.
 * @version 1.0
 * @since 22-11-2024
 */

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final FinesRepository finesRepository;
    private final NotificationRepository notificationRepository;


    /**
     * This method returns a paginated response of fines by a given user id and
     * the page response specifications.
     * @param userId The user id to filter the fines.
     * @param pageNumber the current page number.
     * @param pageSize the page size (max number of fines in the page)
     * @return the page response of fines.
     */
    @Override
    public PaginatedResponseDTO<FineOutputDTO> getFinesByUserId(String userId, int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<FineModel> page = finesRepository.findByUserId(userId, pageable);
        return FineOutputDTO.encapsulateFineModelOnDTO(page);
    }

    /**
     * This method returns the page response of notifications by a given user id.
     * @param userId The user id to filter the notifications
     * @param pageNumber the current page number.
     * @param pageSize the page size (max number of notifications in the page).
     * @return the response of notifications.
     */
    @Override
    public PaginatedResponseDTO<NotificationDTO> getNotifications(String userId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<NotificationModel> page = notificationRepository.findByUserId(userId, pageable);
        return NotificationDTO.encapsulateFineModelOnDTO(page);
    }


    /**
     * Retrieves the number of notifications that have not been seen by a specific user.
     * <p>
     * This method interacts with the repository layer to count notifications that
     * are associated with the given user ID and have not been marked as seen.
     * <p>
     * @param userId the ID of the user whose unseen notifications are to be counted.
     * @return the count of unseen notifications for the specified user.
     */
    @Override
    public UserNotificationsInformationDTO getNumberNotificationsNotSeenByUser(String userId) {
        return new UserNotificationsInformationDTO(
                notificationRepository.getNumberNotificationsNotSeenByUser(userId, false),
                finesRepository.getNumberActiveFinesByUser(userId, FineStatus.PENDING)
        );
    }

    /**
     * Marks a specific notification as seen by updating its `hasBeenSeen` status to true.
     * <p>
     * This method interacts with the repository layer to update the state of a notification
     * identified by its unique ID. The method returns the number of records updated in the database.
     * <p>
     * @param notificationId the unique identifier of the notification to be marked as seen.
     * @return the number of notifications updated (1 if successful, 0 if no matching notification was found).
     */
    @Override
    public int markNotificationAsSeen(String notificationId) {
        return notificationRepository.markNotificationAsSeen(notificationId);
    }

}