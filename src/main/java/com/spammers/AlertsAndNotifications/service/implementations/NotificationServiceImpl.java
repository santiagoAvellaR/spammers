package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.exceptions.SpammersPublicExceptions;

import com.spammers.AlertsAndNotifications.model.LoanDTO;
import com.spammers.AlertsAndNotifications.model.LoanModel;
import com.spammers.AlertsAndNotifications.model.NotificationModel;
import com.spammers.AlertsAndNotifications.model.UserInfo;
import com.spammers.AlertsAndNotifications.model.*;
import com.spammers.AlertsAndNotifications.model.enums.EmailTemplate;
import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import com.spammers.AlertsAndNotifications.repository.FinesRepository;
import com.spammers.AlertsAndNotifications.repository.LoanRepository;
import com.spammers.AlertsAndNotifications.repository.NotificationRepository;
import com.spammers.AlertsAndNotifications.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import java.util.Optional;



/**
 * This service provides the Notifications features, in order to handle the
 * possible notifications to the user's guardian and handle Fines.
 * @version 1.0
 * @since 22-11-2024
 */

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final FinesRepository finesRepository;
    private final LoanRepository loanRepository;
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;
    private final ApiClient apiClient;
    private int page = 0;


    @Scheduled(cron = "0 30 8 * * ?")
    private void checkLoans(){
        Pageable pageable = PageRequest.of(page, 15);
        List<LoanModel> loans = loanRepository.findLoansExpiringInExactlyNDays(LocalDate.now().plusDays(3), pageable);
        for(LoanModel loan : loans){

            //emailService.sendEmail(loan.getLoanId(), );
        }
    }

    /**
     * This method creates a Notification of the Loan. Saves it into Loans Table where we have just active loans.
     * @param loanDTO The Loan DTO to create and send the notification.
     * @throws SpammersPublicExceptions If there is any problem with your
     * @throws SpammersPrivateExceptions
     */
    @Override
    public void notifyLoan(LoanDTO loanDTO) throws SpammersPublicExceptions, SpammersPrivateExceptions {
        String email = loanDTO.getEmailGuardian();
        LocalDate returnDate = loanDTO.getLoanReturn();

        NotificationModel notification = new NotificationModel(loanDTO.getUserId(), email,returnDate);
        LoanModel loanM = new LoanModel(loanDTO.getUserId(),loanDTO.getBookId(),LocalDate.now(),loanDTO.getBookName(),returnDate,true);

        notificationRepository.save(notification);
        loanRepository.save(loanM);
        emailService.sendEmailTemplate(email,EmailTemplate.NOTIFICATION_ALERT,"Préstamo realizado con fecha de devolucion: "+returnDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    /**
     * This method closes a loan if it has not a fine associated with PENDING status.
     * @param bookId the Loan id.
     * @param userId The User id.
     * @throws SpammersPublicExceptions If loan is not found in the Database
     * @throws SpammersPrivateExceptions If the Loan has a current fine with PENDING status
     */
    @Override
    public void closeLoan(String bookId, String userId) throws SpammersPublicExceptions, SpammersPrivateExceptions {
        Optional<LoanModel> loan  = loanRepository.findLoanByUserAndBookId(userId,bookId);
        if(loan.isEmpty()) throw new SpammersPrivateExceptions(SpammersPrivateExceptions.LOAN_NOT_FOUND);
        List<FineModel> fines  = finesRepository.findByLoanId(loan.get().getLoanId());
        if(!fines.isEmpty() && pendingFine(fines)) throw new SpammersPublicExceptions(SpammersPublicExceptions.FINE_PENDING);
        loanRepository.delete(loan.get());
        String email = apiClient.getUserInfoById(userId).getGuardianEmail();
        emailService.sendEmailTemplate(email,EmailTemplate.NOTIFICATION_ALERT,"Devolución de libro: "+loan.get().getBookName() +
                " \nFecha: "+LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        NotificationModel notificationModel = new NotificationModel(userId, email,LocalDate.now());
        notificationRepository.save(notificationModel);
    }

    @Override
    public void returnBook(String bookId, boolean returnedInBadCondition) {
        Optional<LoanModel> loanModel = loanRepository.findLoanByBookIdAndBookReturned(bookId, returnedInBadCondition);
        if(loanModel.isEmpty()){
            throw new SpammersPrivateExceptions(SpammersPrivateExceptions.LOAN_NOT_FOUND);
        }
        UserInfo userInfo = apiClient.getUserInfoById(loanModel.get().getUserId());
        int days = daysDifference(loanModel.get().getLoanDate());
        String emailBody = buildEmailBody(loanModel.get().getLoanDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                userInfo.getGuardianEmail(), userInfo.getName(), loanModel.get().getStatus(), returnedInBadCondition, days);
        emailService.sendEmailCustomised(userInfo.getGuardianEmail(), "Devolución de un libro", emailBody);
    }

    @Override
    public List<FineModel> getFines(String userId) {
        return List.of();
    }

    @Override
    public List<NotificationModel> getNotifications(String userId) {
        return List.of();
    }
    /**
     * This method sendEmails every 10 minutes between 10 - 12 Monday to Friday.
     * With Pagination of the Query for better performance.
     */
    @Scheduled(cron = "0 */10 10-12 * * MON-FRI")
    private void checkLoansThreeDays(){
        processEmails();
        page = page > 18 ? 0 : page+1;
    }

    private int daysDifference(LocalDate deadline){
        return LocalDate.now().isAfter(deadline) ? (int) ChronoUnit.DAYS.between(deadline, LocalDate.now()): 0;
    }
    private void processEmails(){
        List<LoanModel> loans = fetchEmailsToSend();
        for(LoanModel loan : loans){
            sendEmail(loan);
        }
    }

    private  String buildEmailBody(String loanDate, String guardianName, String studentName, boolean statusLoan, boolean badCondition, int delay) {

        String delayMessage = !statusLoan ? "Sin embargo, tuvo un retraso de " + delay + " días.\n" : "";
        String conditionMessage = badCondition ? "Además, el libro se devolvió en malas condiciones.\n" : "";


        return String.format(
                EmailTemplate.BOOK_RETURN.getTemplate(),
                guardianName,
                studentName,
                loanDate,
                delayMessage,
                conditionMessage
        );
    }

    private void sendEmail(LoanModel loan) {
        UserInfo userInfo = apiClient.getUserInfoById(loan.getUserId());
        emailService.sendEmailTemplate(userInfo.getGuardianEmail(),EmailTemplate.NOTIFICATION_ALERT
                ,"Estudiante: " + userInfo.getName() + "tiene 3 dias para devolver el libro, de lo contrario se generará una multa.");
        NotificationModel notificationModel = new NotificationModel(loan.getUserId(),userInfo.getGuardianEmail(),LocalDate.now());
        notificationRepository.save(notificationModel);
    }

    private List<LoanModel> fetchEmailsToSend(){
        int pageSize = 15;
        Pageable pageable = PageRequest.of(page, pageSize);
        return loanRepository.findLoansExpiringInExactlyNDays(LocalDate.now(),pageable);
    }


    private boolean pendingFine(List<FineModel> fines) {
        boolean pending = false;
        for (FineModel fine : fines) {
            if(fine.getFineStatus().equals(FineStatus.PENDING)){
                pending = true;
                break;
            }
        }
        return pending;
    }
}
