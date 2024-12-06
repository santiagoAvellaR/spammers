package com.spammers.AlertsAndNotifications.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Setter
@Getter
public class ErrorDetails {
    private String message;
    private LocalDateTime date;
}
