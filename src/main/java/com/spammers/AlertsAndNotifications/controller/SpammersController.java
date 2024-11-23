package com.spammers.AlertsAndNotifications.controller;


import com.spammers.AlertsAndNotifications.model.FineModel;
import com.spammers.AlertsAndNotifications.model.LoanDTO;
import com.spammers.AlertsAndNotifications.model.NotificationModel;
import com.spammers.AlertsAndNotifications.service.interfaces.EmailService;
import com.spammers.AlertsAndNotifications.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class SpammersController {


    private final NotificationService notificationService;

    /**
     * This method returns the notifications of a given user
     * @param userId The user id
     * @return the notifications associated to the user.
     */
    @GetMapping("/notifications")
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationModel> getNotifications(@RequestParam String userId){
        return notificationService.getNotifications(userId);
    }

    /**
     * This method returns the fines of a given user.
     * @param userId The user id
     * @return the fines of the user.
     */
    @GetMapping("/fines")
    @ResponseStatus(HttpStatus.OK)
    public List<FineModel> getFines(@RequestParam String userId){
        return notificationService.getFines(userId);
    }

    /**
     * This method sends a notification of a loan created.
     * @param loanDTO the information required to send the notification:
     *                (userId, bookId, email of the Parent, book name and the
     *                return date)
     * @return A message of successfully sent notification.
     */
    @PostMapping("/notify-loan")
    @ResponseStatus(HttpStatus.OK)
    public String notifyLoan(@RequestBody LoanDTO loanDTO){
        notificationService.notifyLoan(loanDTO);
        return "Notification Sent!";
    }

    /**
     * This method sends a notification when the Loan is returned (closed)
     * @param bookId The book id
     * @param userId the user id
     * @return a successful message when
     */
    @PutMapping("/close-loan")
    @ResponseStatus(HttpStatus.OK)
    public String closeLoan(@RequestParam String bookId, @RequestParam String userId){
        notificationService.closeLoan(bookId,userId);
        return "Loan Closed!";
    }

}