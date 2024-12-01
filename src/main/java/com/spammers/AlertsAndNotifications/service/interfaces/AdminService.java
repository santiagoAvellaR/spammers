package com.spammers.AlertsAndNotifications.service.interfaces;

import java.time.LocalDate;
import java.util.Map;

public interface AdminService {
    Map<String, Object> returnAllActiveFines(int pageSize, int pageNumber);
    Map<String, Object> returnAllActiveFinesBetweenDate(LocalDate date, int pageSize, int pageNumber);
}
