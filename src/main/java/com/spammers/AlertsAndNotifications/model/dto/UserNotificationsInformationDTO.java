package com.spammers.AlertsAndNotifications.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserNotificationsInformationDTO {
    private Long numberNotificationsNotSeen;
    private Long numberActiveFines;
}
