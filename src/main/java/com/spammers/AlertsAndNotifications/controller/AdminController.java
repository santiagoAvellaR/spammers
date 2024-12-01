package com.spammers.AlertsAndNotifications.controller;

import com.spammers.AlertsAndNotifications.model.FineModel;
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
    private final NotificationService notificationService;

    @GetMapping("/fines-last-month")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getFinesLastMonth(@RequestParam int months, @RequestParam int page,@RequestParam int size){
        //return notificationService.finesLastMonth(months, page, size);
        return Map.of();
    }

    @GetMapping("/loans-about-expire")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getLoansAboutExpire(@RequestParam LocalDate date, @RequestParam int page, int size){
        return notificationService.returnAllActiveFinesBetweenDate(date, size, page);
    }

    @GetMapping("/fines-pending")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getFinesPending(@RequestParam int page, int size){
        return notificationService.returnAllActiveFines(page, size);
    }

    /**
     * This method returns the fines of a given user.
     * @param userId The user id
     * @return the fines of the user.
     */
    @GetMapping("/users/{userId}/fines")
    @ResponseStatus(HttpStatus.OK)// TODO --> Debería usar paginacion al igual que todo metodo de consulta
    public List<FineModel> getFines(@PathVariable String userId){
        return notificationService.getFines(userId);
    }



}
