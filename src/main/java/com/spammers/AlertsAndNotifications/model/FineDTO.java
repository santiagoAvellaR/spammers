package com.spammers.AlertsAndNotifications.model;

import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import com.spammers.AlertsAndNotifications.model.enums.FineType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class FineDTO {
    private String fineId;
    private LoanModel loan;
    private String description;
    private float amount;
    private LocalDate expiredDate;
    private FineStatus fineStatus;
    private FineType fineType;
    private List<FineNotification> fineNotifications;
}
