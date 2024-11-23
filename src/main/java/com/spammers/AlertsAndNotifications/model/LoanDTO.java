package com.spammers.AlertsAndNotifications.model;



import lombok.Getter;

import java.time.LocalDate;
// Esta clase para lo que deberia tener el prestamo que nos envia el modulo de gestion de prestamos
@Getter
public class LoanDTO {
    private String loanId;
    private String userId;
    private String bookId;
    private LocalDate loanDate;
    private boolean status;
    private LocalDate loanExpired;

    public boolean getStatus() {
        return status;
    }
}
