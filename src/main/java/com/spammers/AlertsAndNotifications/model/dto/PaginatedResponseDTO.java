package com.spammers.AlertsAndNotifications.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class PaginatedResponseDTO<T> {
    private List<T> data;
    private int currentPage;
    private int totalPages;
    private long totalItems;
}
