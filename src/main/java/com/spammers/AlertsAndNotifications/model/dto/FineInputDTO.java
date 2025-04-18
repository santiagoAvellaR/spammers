package com.spammers.AlertsAndNotifications.model.dto;

import com.spammers.AlertsAndNotifications.model.enums.FineType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class FineInputDTO {
    private float amount;
    private FineType fineType;
    private String bookId;
    private String userId;
    private String description;
}
