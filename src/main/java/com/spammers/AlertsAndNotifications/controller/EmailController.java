package com.spammers.AlertsAndNotifications.controller;


import com.spammers.AlertsAndNotifications.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/sendEmail")
    @ResponseStatus(HttpStatus.OK)
    public String enviarCorreo(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        emailService.sendEmail(to, subject, body);
        return "Correo enviado!";
    }
}