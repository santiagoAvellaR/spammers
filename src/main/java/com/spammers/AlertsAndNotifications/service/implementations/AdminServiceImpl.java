package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.model.*;
import com.spammers.AlertsAndNotifications.model.dto.*;
import com.spammers.AlertsAndNotifications.model.enums.*;
import com.spammers.AlertsAndNotifications.repository.FinesRepository;
import com.spammers.AlertsAndNotifications.repository.LoanRepository;
import com.spammers.AlertsAndNotifications.repository.NotificationRepository;
import com.spammers.AlertsAndNotifications.service.interfaces.AdminService;
import com.spammers.AlertsAndNotifications.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
/**
 * This class implements the Admin Service. Providing the
 * features to administrate the fines and notifications.
 * @since 12-12-2024
 * @version 1.0
 */
@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {
    private final FinesRepository finesRepository;
    private final FineDailyIncrease fineDailyIncrease;
    private final LoanRepository loanRepository;
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;
    private final ApiClient apiClient;

    /**
     * This method returns a book loan by providing the book id a boolean to indicate
     * if the book was returned in a bad condition or not.
     * @param bookId the book id to search book.
     * @param returnedInBadCondition The given condition of the book, true if it was returned in bad condition,
     *                               false otherwise.
     */
    @Override
    public void returnBook(String bookId, boolean returnedInBadCondition) {
        Optional<LoanModel> loanModel = loanRepository.findLoanByBookIdAndBookReturned(bookId, false);
        if(loanModel.isEmpty()){
            throw new SpammersPrivateExceptions(SpammersPrivateExceptions.LOAN_NOT_FOUND, 404);
        }
        //if(loanModel.get().getFines().stream().anyMatch(fineModel -> fineModel.getFineStatus().equals(FineStatus.PENDING))) throw new SpammersPrivateExceptions("THE LOAN HAS PENDING FINES, IT CAN'T BE RETORNED",404);
        UserInfo userInfo = apiClient.getUserInfoById(loanModel.get().getUserId());
        int days = daysDifference(loanModel.get().getLoanDate());
        String emailBody = buildEmailBody(loanModel.get().getLoanDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                userInfo.getGuardianName(), userInfo.getName(), loanModel.get().getStatus(), returnedInBadCondition, days);
        emailService.sendEmailCustomised(userInfo.getGuardianEmail(), "Devolución de un libro", emailBody);
        loanModel.get().setBookReturned(true);
        loanRepository.save(loanModel.get());
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
    public void openFine(FineInputDTO fineInputDTO) throws SpammersPrivateExceptions {
        Optional<LoanModel> lastLoan = loanRepository.findLastLoan(fineInputDTO.getBookId(), fineInputDTO.getUserId());
        if (lastLoan.isPresent()) {
            LoanModel loan = lastLoan.get();
            LocalDate currentDate = LocalDate.now();
            UserInfo userInfo = apiClient.getUserInfoById(fineInputDTO.getUserId());
            String email = userInfo.getGuardianEmail();
            String studentName = userInfo.getName();
            String description;
            if(fineInputDTO.getDescription()==null || fineInputDTO.getDescription().isBlank()){
                description = fineInputDTO.getFineType() == FineType.DAMAGE ? FineDescription.DAMAGED_MATERIAL.getDescription() : FineDescription.RETARDMENT.getDescription();
            } else description = fineInputDTO.getDescription();
            FineModel fineModel = FineModel.builder().loan(loan).description(description).amount(fineInputDTO.getAmount()).expiredDate(currentDate).fineStatus(FineStatus.PENDING).fineType(fineInputDTO.getFineType()).studentName(studentName).guardianEmail(email).build();
            finesRepository.save(fineModel);
            NotificationModel notification = new FineNotification(loan.getUserId(), email, currentDate, NotificationType.FINE, fineModel, false, fineModel.getLoan().getBookName());
            notificationRepository.save(notification);
            emailService.sendEmailTemplate(email, EmailTemplate.FINE_ALERT, "Se ha registrado una nueva multa: ", fineInputDTO.getAmount(), currentDate, description);
        }
        else {
            throw new SpammersPrivateExceptions(SpammersPrivateExceptions.LOAN_NOT_FOUND, 404);
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
            throw new SpammersPrivateExceptions(SpammersPrivateExceptions.FINE_NOT_FOUND, 404);
        }
    }

    /**
     * Retrieves all active fines (with status PENDING) and returns them in a paginated response.
     *
     * @param pageSize The number of records per page.
     * @param pageNumber The page number to retrieve.
     * @return A PaginatedResponseDTO containing the list of active fines and pagination details.
     */
    @Override
    public PaginatedResponseDTO<FineOutputDTO> returnAllActiveFines(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<FineModel> page = finesRepository.findByStatus(FineStatus.PENDING, pageable);
        return FineOutputDTO.encapsulateFineModelOnDTO(page);
    }
    /**
     * This method creates a Notification of the Loan. Saves it into Loans Table where we have just active loans.
     * @param loanDTO The Loan DTO to create and send the notification.
     * @throws SpammersPrivateExceptions
     */
    @Override
    public void notifyLoan(LoanDTO loanDTO) throws SpammersPrivateExceptions {
        String email = loanDTO.getEmailGuardian();
        LocalDate returnDate = loanDTO.getLoanReturn();
        UserInfo userInfo = apiClient.getUserInfoById(loanDTO.getUserId());
        LoanModel loanM = new LoanModel(loanDTO.getUserId(),loanDTO.getBookId(),LocalDate.now(),loanDTO.getBookName(),returnDate,true);
        loanRepository.save(loanM);
        NotificationModel notification = new LoanNotification(loanDTO.getUserId(), email, returnDate, NotificationType.BOOK_LOAN,loanM, false, loanM.getBookName());

        notificationRepository.save(notification);
        emailService.sendEmailTemplate(email,EmailTemplate.NOTIFICATION_ALERT,"Préstamo realizado a: "+userInfo.getName()+"\nArtículo: " +loanDTO.getBookName()+
                "\nFecha de devolucion: "+returnDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    /**
     * Retrieves all active fines (with status PENDING) within a specific date
     * and returns them in a paginated response.
     *
     * @param date The date to filter fines, according to the year and month of the date.
     * @param pageSize The number of records per page.
     * @param pageNumber The page number to retrieve.
     * @return A PaginatedResponseDTO containing the list of active fines and pagination details.
     */
    @Override
    public PaginatedResponseDTO<FineOutputDTO> returnAllActiveFinesBetweenDate(LocalDate date, int pageSize, int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<FineModel> page = finesRepository.findByStatusAndDate(FineStatus.PENDING, date, pageable);
        return FineOutputDTO.encapsulateFineModelOnDTO(page);
    }

    /**
     * This method allows the Admin to change the fines day rate.
     * @param rate the rate of the fines.
     */
    @Override
    public void setFinesRateDay(float rate) {
        fineDailyIncrease.setFineRate(rate);
    }

    /**
     * This method returns the fines day rate.
     * @return float The fines day rate.
     */
    @Override
    public float getFinesDayRate() {
        return fineDailyIncrease.getFineRate();
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


}
