package com.spammers.AlertsAndNotifications.model.dto;

import com.spammers.AlertsAndNotifications.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class NotificationDTO {
    private String id;
    private LocalDate sentDate;
    private NotificationType notificationType;
    private String bookName;
    private boolean hasBeenSeen;
}
