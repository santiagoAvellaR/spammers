package com.spammers.AlertsAndNotifications.controller;

import com.spammers.AlertsAndNotifications.model.dto.FineOutputDTO;
import com.spammers.AlertsAndNotifications.model.dto.PaginatedResponseDTO;
import com.spammers.AlertsAndNotifications.service.interfaces.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications/admin")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/loans-about-expire")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedResponseDTO<FineOutputDTO> getPendingFinesByDate(@RequestParam LocalDate date,
                                                                     @RequestParam int page, @RequestParam int size){
        return adminService.returnAllActiveFinesBetweenDate(date, size, page);
    }

    @GetMapping("/fines-pending")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedResponseDTO<FineOutputDTO> getPendingFines(@RequestParam int page, @RequestParam int size){
        return adminService.returnAllActiveFines(page, size);
    }
}
