package com.spammers.AlertsAndNotifications.service.interfaces;

import com.spammers.AlertsAndNotifications.model.dto.FineInputDTO;
import com.spammers.AlertsAndNotifications.model.dto.FineOutputDTO;
import com.spammers.AlertsAndNotifications.model.dto.LoanDTO;
import com.spammers.AlertsAndNotifications.model.dto.PaginatedResponseDTO;

import java.time.LocalDate;

public interface AdminService {
    void notifyLoan(LoanDTO loanDTO, String token);
    void returnBook(String bookId, boolean returnedInBadCondition, String token);
    void openFine(FineInputDTO fineInputDTO, String token);
    void closeFine(String loanId, String token);
    PaginatedResponseDTO<FineOutputDTO> returnAllActiveFines(int pageSize, int pageNumber);
    PaginatedResponseDTO<FineOutputDTO> returnAllActiveFinesBetweenDate(LocalDate date, int pageSize, int pageNumber);
    void setFinesRateDay(float rate);
    float getFinesDayRate();
}
