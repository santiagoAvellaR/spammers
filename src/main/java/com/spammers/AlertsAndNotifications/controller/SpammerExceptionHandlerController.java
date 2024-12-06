package com.spammers.AlertsAndNotifications.controller;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.model.dto.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class SpammerExceptionHandlerController {

    @ExceptionHandler(SpammersPrivateExceptions.class)
    public ResponseEntity<ErrorDetails> handleSpammersPrivateExceptions(SpammersPrivateExceptions ex){
        ErrorDetails errorDetails = ErrorDetails
                .builder()
                .message(ex.getMessage())
                .date(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatusCode.valueOf(ex.code));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDetails> handleNotFound(NoResourceFoundException ex) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(ex.getMessage())
                .date(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGeneralException(Exception ex) {
        System.out.println(ex);
        ErrorDetails errorDetails = ErrorDetails
                .builder()
                .message(ex.getMessage())
                .date(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
