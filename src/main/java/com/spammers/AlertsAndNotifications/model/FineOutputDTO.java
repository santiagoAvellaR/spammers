package com.spammers.AlertsAndNotifications.model;

import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import com.spammers.AlertsAndNotifications.model.enums.FineType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class FineOutputDTO {
    private String fineId;
    private String description;
    private float amount;
    private FineStatus fineStatus;
    private FineType fineType;
    private LocalDate expiredDate;
    private String bookTitle;
}
