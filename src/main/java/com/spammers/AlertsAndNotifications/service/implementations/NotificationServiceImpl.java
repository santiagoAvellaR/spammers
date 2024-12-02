package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.exceptions.SpammersPublicExceptions;
import com.spammers.AlertsAndNotifications.model.dto.LoanDTO;
import com.spammers.AlertsAndNotifications.model.LoanModel;
import com.spammers.AlertsAndNotifications.model.NotificationModel;
import com.spammers.AlertsAndNotifications.model.UserInfo;
import com.spammers.AlertsAndNotifications.model.*;
import com.spammers.AlertsAndNotifications.model.dto.NotificationDTO;
import com.spammers.AlertsAndNotifications.model.dto.PaginatedResponseDTO;
import com.spammers.AlertsAndNotifications.model.enums.*;
import com.spammers.AlertsAndNotifications.repository.FinesRepository;
import com.spammers.AlertsAndNotifications.repository.LoanRepository;
import com.spammers.AlertsAndNotifications.repository.NotificationRepository;
import com.spammers.AlertsAndNotifications.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    private final ApiClient apiClient=new ApiClientLocal(RestClient.builder().build());
    private final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

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

        LoanModel loanM = new LoanModel(loanDTO.getUserId(),loanDTO.getBookId(),LocalDate.now(),loanDTO.getBookName(),returnDate,true);
        loanRepository.save(loanM);
        NotificationModel notification = new LoanNotification(loanDTO.getUserId(), email, returnDate, NotificationType.BOOK_LOAN,loanM, false, loanM.getBookName());

        notificationRepository.save(notification);
        emailService.sendEmailTemplate(email,EmailTemplate.NOTIFICATION_ALERT,"Préstamo realizado con fecha de devolucion: "+returnDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    public void returnBook(String bookId, boolean returnedInBadCondition) {
        Optional<LoanModel> loanModel = loanRepository.findLoanByBookIdAndBookReturned(bookId, false);
        if(loanModel.isEmpty()){
            throw new SpammersPrivateExceptions(SpammersPrivateExceptions.LOAN_NOT_FOUND);
        }
        UserInfo userInfo = apiClient.getUserInfoById(loanModel.get().getUserId());
        int days = daysDifference(loanModel.get().getLoanDate());
        String emailBody = buildEmailBody(loanModel.get().getLoanDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                userInfo.getGuardianEmail(), userInfo.getName(), loanModel.get().getStatus(), returnedInBadCondition, days);
        emailService.sendEmailCustomised(userInfo.getGuardianEmail(), "Devolución de un libro", emailBody);
        loanModel.get().setBookReturned(true);
        loanRepository.save(loanModel.get());
    }

    @Override
    public PaginatedResponseDTO<FineOutputDTO> getFinesByUserId(String userId, int pageSize, int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<FineModel> page = finesRepository.findByUserId(userId, pageable);
        return FineOutputDTO.encapsulateFineModelOnDTO(page);
    }

    /**
     * Opens a new fine for a specific loan and notifies the associated user.
     *
     * This method creates a new fine for the specified loan, sends a notification
     * to the user, and triggers an email alert with the fine details. The fine is
     * saved with a status of {@link FineStatus#PENDING}.
     *
     * @param fineInputDTO A DTO of the fine.
     */
    @Override
    public void openFine(FineInputDTO fineInputDTO) throws SpammersPublicExceptions, SpammersPrivateExceptions {
        Optional<LoanModel> lastLoan = loanRepository.findLastLoan(fineInputDTO.getBookId(), fineInputDTO.getUserId());
        if (lastLoan.isPresent()) {
            LoanModel loan = lastLoan.get();
            LocalDate currentDate = LocalDate.now();
            String email = apiClient.getUserInfoById(fineInputDTO.getUserId()).getGuardianEmail();
            String description = fineInputDTO.getFineType() == FineType.DAMAGE ? FineDescription.DAMAGED_MATERIAL.getDescription() : FineDescription.RETARDMENT.getDescription();
            FineModel fineModel = FineModel.builder().loan(loan).description(description).amount(fineInputDTO.getAmount()).expiredDate(currentDate).fineStatus(FineStatus.PENDING).fineType(fineInputDTO.getFineType()).build();
            finesRepository.save(fineModel);
            NotificationModel notification = new FineNotification(loan.getUserId(), email, currentDate, NotificationType.FINE, fineModel, false, fineModel.getLoan().getBookName());
            notificationRepository.save(notification);
            emailService.sendEmailTemplate(email, EmailTemplate.FINE_ALERT, "Se ha registrado una nueva multa: ", fineInputDTO.getAmount(), currentDate, description);
        }
        else {
            throw new SpammersPrivateExceptions(SpammersPrivateExceptions.LOAN_NOT_FOUND);
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
    public void closeFine(String fineId) throws SpammersPrivateExceptions {
        Optional<FineModel> fineOptional = finesRepository.findById(fineId);
        if (fineOptional.isPresent()) {
            finesRepository.updateFineStatus(fineId, FineStatus.PAID);
            FineModel fineModel = fineOptional.get();
            LocalDate currentDate = LocalDate.now();
            UserInfo userInfo = apiClient.getUserInfoById(fineModel.getLoan().getUserId());
            String email = userInfo.getGuardianEmail();
            NotificationModel notification = new NotificationModel(fineModel.getLoan().getUserId(), email, currentDate, NotificationType.FINE_PAID, false, fineModel.getLoan().getBookName());
            notificationRepository.save(notification);
            emailService.sendEmailTemplate(email, EmailTemplate.FINE_ALERT, "Se ha cerrado una multa: ", fineModel.getAmount(), currentDate, fineModel.getDescription());
        } else{
            throw new SpammersPrivateExceptions(SpammersPrivateExceptions.FINE_NOT_FOUND);
        }
    }
    @Override
    public PaginatedResponseDTO<NotificationDTO> getNotifications(String userId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<NotificationModel> page = notificationRepository.findByUserId(userId, pageable);
        return NotificationDTO.encapsulateFineModelOnDTO(page);
    }

    private int daysDifference(LocalDate deadline){
        return LocalDate.now().isAfter(deadline) ? (int) ChronoUnit.DAYS.between(deadline, LocalDate.now()): 0;
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