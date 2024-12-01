package com.spammers.AlertsAndNotifications.controller;

import com.spammers.AlertsAndNotifications.model.FineModel;
import com.spammers.AlertsAndNotifications.service.interfaces.AdminService;
import com.spammers.AlertsAndNotifications.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications/admin")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/loans-about-expire")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getLoansAboutExpire(@RequestParam LocalDate date, @RequestParam int page, int size){
        return adminService.returnAllActiveFinesBetweenDate(date, size, page);
    }

    @GetMapping("/fines-pending")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getFinesPending(@RequestParam int page, int size){
        return adminService.returnAllActiveFines(page, size);
    }
}
