package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.exceptions.SpammersPublicExceptions;
import com.spammers.AlertsAndNotifications.model.LoanDTO;
import com.spammers.AlertsAndNotifications.model.LoanModel;
import com.spammers.AlertsAndNotifications.model.NotificationModel;
import com.spammers.AlertsAndNotifications.model.UserInfo;
import com.spammers.AlertsAndNotifications.model.enums.EmailTemplate;
import com.spammers.AlertsAndNotifications.repository.FinesRepository;
import com.spammers.AlertsAndNotifications.repository.LoanRepository;
import com.spammers.AlertsAndNotifications.repository.NotificationRepository;
import com.spammers.AlertsAndNotifications.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final FinesRepository finesRepository;
    private final LoanRepository loanRepository;
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;
    private final ApiClient apiClient;

    @Scheduled(cron = "0 30 8 * * ?")
    private void checkLoans(){
        List<LoanModel> loans = loanRepository.findLoansExpiringInExactlyNDays(LocalDate.now().plusDays(3));
        for(LoanModel loan : loans){

            //emailService.sendEmail(loan.getLoanId(), );
        }
    }

    /**
     * This method creates a Notification of the Loan. Saves it into Loans Table where we have just active loans.
     * @param idBook the id of the book borrowed.
     * @param loan the loan DTO Object
     * @param email the parent email to send the notification.
     * @param fineRate the fine rate per day, in case the loan expires.
     */
    @Override
    public void createLoan(String idBook, LoanDTO loan, String email, float fineRate) throws SpammersPublicExceptions, SpammersPrivateExceptions {
        if(validate(fineRate)) throw new SpammersPublicExceptions(SpammersPublicExceptions.WRONG_FINE_RATE);
        NotificationModel notification = new NotificationModel(loan.getUserId(), email,loan.getLoanExpired());
        LoanModel loanM = new LoanModel(loan.getUserId(),loan.getBookId(),loan.getLoanDate(),loan.getLoanExpired(),loan.getStatus());
        notificationRepository.save(notification);
        loanRepository.save(loanM);
        emailService.sendEmailTemplate(email,EmailTemplate.NOTIFICATION_ALERT,"Préstamo realizado.");
    }

    private boolean validate(float fineRate) {
        return fineRate <= 0;
    }

    @Override
    public void closeLoan(String idLoan) {

    }

    @Override
    public void closeFine(String idLoan) {

    }
    @Override
    public void returnBook(LoanDTO loan, boolean returnedInBadCondition) {
        UserInfo userInfo = apiClient.getUserInfoById(loan.getUserId());
        LocalDate dateLoan = loan.getLoanDate();
        DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String formatteDate = dateLoan.format(formatter);
        int days = daysDifference(loan.getLoanExpired());
        String emailBody = buildEmailBody(formatteDate, userInfo.getGuardianEmail(), userInfo.getName(), loan.getStatus(), returnedInBadCondition, days);
        emailService.sendEmailCustomised(userInfo.getGuardianEmail(), "Devolución de un libro", emailBody);
    }
    private int daysDifference(LocalDate deadline){
        return LocalDate.now().isAfter(deadline) ? (int) ChronoUnit.DAYS.between(deadline, LocalDate.now()): 0;
    }
    private String buildEmailBody(String loanDate, String guardianName, String studentName, boolean statusLoan, boolean badCondition, int delay){
        String emailbody = "Buen dia, " + guardianName + "le informamos que el dia de hoy el estudiante " + studentName + "\n" +
                " ha hecho la devolución de un libro que tomo a préstamo el dia " + loanDate + ",\n";
        if(!statusLoan){
            emailbody += "sin embargo tuvo un retraso de " + delay + " dias";
        }
        if(badCondition){
            emailbody += "el libro se devolvio en malas condicones.\n";
        }
        emailbody += "Gracias,\nCordial Saludo.\nEste es el gestor de notificaciones de Biblosoft\n";
        emailbody += "No responder a esta cuenta de correo ya que es enviada por un motor de notificaciones automáticas.";
        return emailbody;
    }

}
