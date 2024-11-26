package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.model.LoanModel;
import com.spammers.AlertsAndNotifications.model.NotificationModel;
import com.spammers.AlertsAndNotifications.model.UserInfo;
import com.spammers.AlertsAndNotifications.model.enums.NotificationType;
import com.spammers.AlertsAndNotifications.repository.LoanRepository;
import com.spammers.AlertsAndNotifications.repository.NotificationRepository;
import com.spammers.AlertsAndNotifications.service.interfaces.EmailService;
import com.spammers.AlertsAndNotifications.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This class provides the daily check of expired loans.
 * @since 21-11-2024
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class LoanExpiredNotification {
    private final LoanRepository loanRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final ApiClient apiClient;
    private final int EXECUTIONS = 15;
    private int page = 0;
    
    /**
     * This method sendEmails every 10 in range [8-10] A.M. Monday - Friday
     * With Pagination from the Database for better performance.
     */
    @Scheduled(cron = "0 */10 8-10 * * MON-FRI")
    private void sendEmails() {
        processEmails();
        //Current time
        LocalTime now = LocalTime.now();
        // Define the time 10:50am
        LocalTime comparisonTime = LocalTime.of(10, 50);
        if (now.isAfter(comparisonTime) || now.equals(comparisonTime)) {
            page = 0;
        }
    }

    private void processEmails() {
        List<LoanModel> loans = fetchEmailsToSend();
        if(loans.isEmpty()){
            return;
        }
        for (LoanModel loan : loans) {

            sendEmail(loan);
        }
    }

    private List<LoanModel> fetchEmailsToSend() {
        Pageable pageable = PageRequest.of(page, EXECUTIONS, Sort.by("loanExpired").ascending());
        return loanRepository.findExpiredLoans(LocalDate.now(), pageable);
    }

    private void sendEmail(LoanModel loan) {
        UserInfo userInfo = apiClient.getUserInfoById(loan.getUserId());
        String emailBody = String.format("""
                Buen día,             
                Nos permitimos informar que su representado, %s, tomó prestado un libro el día %s y, a la fecha, este aún no ha sido devuelto. Agradecemos que gestione su entrega a la mayor brevedad posible.                        
                Quedamos atentos a su pronta respuesta.                     
                Gracias por su atención.
                Cordial saludo.
                Este es el gestor de notificaciones de BibloSoft.
                No responder a esta cuenta de correo ya que es enviada por un motor de notificaciones automáticas."""
                , userInfo.getName(), loan.getLoanDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        emailService.sendEmailCustomised(userInfo.getGuardianEmail(), "Expiración préstamo libro", emailBody);
        NotificationModel notification = new NotificationModel(loan.getUserId(), userInfo.getGuardianEmail()
                , LocalDate.now(), NotificationType.BOOK_LOAN_EXPIRED);
        notificationRepository.save(notification);
        changeLoanEmailExpiredSent(loan);
    }

    private void changeLoanEmailExpiredSent(LoanModel loanModel){
        loanModel.setStatus(false);
        loanRepository.save(loanModel);
    }
}
