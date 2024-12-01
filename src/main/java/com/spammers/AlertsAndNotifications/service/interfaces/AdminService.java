package com.spammers.AlertsAndNotifications.service.interfaces;

import com.spammers.AlertsAndNotifications.model.FineOutputDTO;
import com.spammers.AlertsAndNotifications.model.dto.PaginatedResponseDTO;

import java.time.LocalDate;
import java.util.Map;

public interface AdminService {
    PaginatedResponseDTO<FineOutputDTO> returnAllActiveFines(int pageSize, int pageNumber);
    PaginatedResponseDTO<FineOutputDTO> returnAllActiveFinesBetweenDate(LocalDate date, int pageSize, int pageNumber);
}
