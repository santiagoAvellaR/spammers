package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.exceptions.SpammersPublicExceptions;
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
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        emailService.sendEmailTemplate(email,EmailTemplate.NOTIFICATION_ALERT,"Préstamo realizado con fecha de devolucion: "+loan.getLoanExpired());
    }

    /**
     * This method closes a loan if it has not a fine associated with PENDING status.
     * @param idLoan the Loan id.
     * @throws SpammersPublicExceptions If loan is not found in the Database
     * @throws SpammersPrivateExceptions If the Loan has a current fine with PENDING status
     */
    @Override
    public void closeLoan(String idLoan) throws SpammersPublicExceptions, SpammersPrivateExceptions {
        Optional<LoanModel> loan  = loanRepository.findById(idLoan);
        if(loan.isEmpty()) throw new SpammersPrivateExceptions(SpammersPrivateExceptions.LOAN_NOT_FOUND);
        List<FineModel> fines  = finesRepository.findByLoanId(idLoan);
        if(fines.isEmpty() || pendingFine(fines)) throw new SpammersPublicExceptions(SpammersPublicExceptions.FINE_PENDING);
        loanRepository.delete(loan.get());
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

    @Override
    public void closeFine(String idLoan) {

    }

    /**
     * This method sendEmails every 10 minutes between 10 - 12 Monday to Friday.
     * With Pagination of the Query for better performance.
     */
    @Scheduled(cron = "0 */10 10-12 * * MON-FRI")
    public void checkLoansThreeDays(){
        processEmails();
        page = page > 18 ? 0 : page+1;
    }

    private void processEmails(){
        List<LoanModel> loans = fetchEmailsToSend();
        for(LoanModel loan : loans){
            sendEmail(loan);
        }
    }

    private void sendEmail(LoanModel loan) {
        UserInfo userInfo = apiClient.getUserInfoById(loan.getUserId());
        emailService.sendEmailTemplate(userInfo.getGuardianEmail(),EmailTemplate.NOTIFICATION_ALERT
                ,"Estudiante: " + userInfo.getName() + "tiene 3 dias para devolver el libro, de lo contrario se generará una multa.");
    }

    private List<LoanModel> fetchEmailsToSend(){
        int pageSize = 15;
        Pageable pageable = PageRequest.of(page, pageSize);
        return loanRepository.findLoansExpiringInExactlyNDays(LocalDate.now(),pageable);
    }

    private boolean validate(float fineRate) {
        return fineRate <= 0;
    }
}

