package com.spammers.AlertsAndNotifications.controller;


import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.model.dto.*;
import com.spammers.AlertsAndNotifications.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/notifications/users")
public class SpammersController {

    private final NotificationService notificationService;

    /**
     * This method returns the notifications of a given user.
     *
     * @param userId The user ID.
     * @param page   The page number for pagination (zero-based index).
     * @param size   The number of items per page.
     * @return A map containing the notifications associated with the user.
     */
    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedResponseDTO<NotificationDTO> getNotifications(
            @PathVariable("userId") String userId,
            @RequestParam int page,
            @RequestParam int size) {
        return notificationService.getNotifications(userId, page, size);
    }

    /**
     * This method returns the fines of a given user.
     * @param userId The user id
     * @return the fines of the user.
     */
    @GetMapping("/fines/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedResponseDTO<FineOutputDTO> getFines(
            @PathVariable("userId") String userId,
            @RequestParam int page,
            @RequestParam int size) {
        return notificationService.getFinesByUserId(userId, page, size);
    }

    /**
     * Marks a notification as seen.
     * <p>
     * This method calls the service layer to mark the notification with the given ID as seen.
     * <p>
     * @param notificationId the ID of the notification to be marked as seen.
     * @return the number of rows that has been actualized.
     */
    @PutMapping("/mark-seen/{notificationId}")
    public int markNotificationAsSeen(@PathVariable String notificationId) {
        return notificationService.markNotificationAsSeen(notificationId);
    }

    /**
     * Retrieves the number of notifications that have not been seen by a specific user.
     * <p>
     * This method retrieves the number of notifications that have not been seen for a user
     * by interacting with the service layer.
     * <p>
     * @param userId the ID of the user whose unseen notifications are to be counted.
     * @return a ResponseEntity containing a ResponseMessage object with the result and the count of unseen notifications
     * and the count of active fines.
     */
    @GetMapping("/count/{userId}")
    public UserNotificationsInformationDTO getNumberNotificationsNotSeenByUser(@PathVariable String userId) {
        return notificationService.getNumberNotificationsNotSeenByUser(userId);
    }
}