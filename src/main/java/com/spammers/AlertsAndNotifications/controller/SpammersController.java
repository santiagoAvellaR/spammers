package com.spammers.AlertsAndNotifications.controller;


import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.model.dto.*;
import com.spammers.AlertsAndNotifications.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications/user")
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
    @GetMapping("/users/{userId}/notifications")
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
    @GetMapping("/users/{userId}/fines")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedResponseDTO<FineOutputDTO> getFines(
            @PathVariable("userId") String userId,
            @RequestParam int page,
            @RequestParam int size) {
        return notificationService.getFinesByUserId(userId, page, size);
    }

}