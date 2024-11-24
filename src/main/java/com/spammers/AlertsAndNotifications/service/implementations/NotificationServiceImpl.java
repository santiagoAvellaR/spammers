package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.exceptions.SpammersPublicExceptions;
import com.spammers.AlertsAndNotifications.model.FineModel;
import com.spammers.AlertsAndNotifications.model.LoanDTO;
import com.spammers.AlertsAndNotifications.model.LoanModel;
import com.spammers.AlertsAndNotifications.model.NotificationModel;
import com.spammers.AlertsAndNotifications.model.enums.EmailTemplate;
import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import com.spammers.AlertsAndNotifications.repository.FinesRepository;
import com.spammers.AlertsAndNotifications.repository.LoanRepository;
import com.spammers.AlertsAndNotifications.repository.NotificationRepository;
import com.spammers.AlertsAndNotifications.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {
    private FinesRepository finesRepository;
    private LoanRepository loanRepository;
    private EmailService emailService;
    private NotificationRepository notificationRepository;
    private final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    public NotificationServiceImpl(FinesRepository finesRepository, LoanRepository loanRepository,NotificationRepository notificationRepository, EmailService emailService) {
        this.finesRepository = finesRepository;
        this.loanRepository = loanRepository;
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

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
    public void closeLoan(String loanId) {
        Optional<LoanModel> loanModel = loanRepository.findByLoanId(loanId);
        if(loanModel.isPresent()){

        }
    }

    /**
     * Opens a new fine for a specific loan and notifies the associated user.
     *
     * This method creates a new fine for the specified loan, sends a notification
     * to the user, and triggers an email alert with the fine details. The fine is
     * saved with a status of {@link FineStatus#PENDING}.
     *
     * @param loanId      The unique identifier of the loan associated with the fine.
     *                    The parameter should be of type {@link String}.
     * @param description A description of the fine. The parameter should not exceed 300 characters.
     * @param amount      The amount to be charged for the fine. Should be of type {@link Float}.
     * @param email       The email address of the user to be notified about the fine.
     *                    The parameter should be of type {@link String}.
     */
    @Override
    public void openFine(String loanId, String description, float amount, String email) {
        Optional<LoanModel> loanOptional = loanRepository.findByLoanId(loanId);
        if (loanOptional.isPresent()) {
            LoanModel loan = loanOptional.get();
            LocalDate currentDate = LocalDate.now();
            NotificationModel notification = new NotificationModel(loan.getUserId(), email, currentDate);
            notificationRepository.save(notification);
            FineModel fineModel = FineModel.builder().loanId(loanId).description(description).amount(amount).expiredDate(currentDate).fineStatus(FineStatus.PENDING).build();
            finesRepository.save(fineModel);
            emailService.sendEmailTemplate(email, EmailTemplate.FINE_ALERT, amount, currentDate, description);
        }
    }

    /**
     * Closes an existing fine by updating its status to {@link FineStatus#PAID}.
     *
     * This method finds the fine by its unique identifier and updates its status
     * to indicate that it has been paid. If no fine is found with the given ID,
     * the method does nothing.
     *
     * @param fineId The unique identifier of the fine to be closed.
     *               The parameter should be of type {@link String}.
     */
    @Override
    public void closeFine(String fineId) {
        Optional<FineModel> fineOptional = finesRepository.findById(fineId);
        if(fineOptional.isPresent()){
            finesRepository.updateFineStatus(fineId, FineStatus.PAID);
        }
    }
}
