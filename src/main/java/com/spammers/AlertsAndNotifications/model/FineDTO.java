package com.spammers.AlertsAndNotifications.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class FineDTO {
    private String description;
    private float amount;
    private String fineType;
    private String bookId;
    private String userId;
}
