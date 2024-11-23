package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.model.LoanModel;
import com.spammers.AlertsAndNotifications.model.UserInfo;
import com.spammers.AlertsAndNotifications.repository.LoanRepository;
import com.spammers.AlertsAndNotifications.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
    private final EmailService emailService;
    private final ApiClient apiClient;
    private final int EXECUTIONS = 15;
    private int page = 0;

    /**
     * This method sendEmails every 10 in range [8-10] A.M. Monday - Friday
     * With Pagination from the Database for better performance.
     */
    @Scheduled(cron = "0 */10 8-10 * * MON-FRI") // Cada 5 minutos de lunes a viernes entre las 8 y las 9:59
    public void sendEmails() {
        processEmails();
        page = page > 18 ? 0 : page+1;
    }

    private void processEmails() {
        List<LoanModel> loans = fetchEmailsToSend();
        for (LoanModel loan : loans) {
            sendEmail(loan);
        }
    }

    private List<LoanModel> fetchEmailsToSend() {
        Pageable pageable = PageRequest.of(page, EXECUTIONS, Sort.by("loanExpired").ascending());
        return loanRepository.findExpiredLoans(LocalDateTime.now(), pageable);
    }

    private void sendEmail(LoanModel loan) {
        UserInfo userInfo = apiClient.getUserInfoById(loan.getUserId());
        emailService.sendEmailCustomised(userInfo.getGuardianEmail(), "Expiración préstamo libro", "Su representado se llevo un libro " +
                "y no lo ha entregado procederemos por la vía legal de ser necesario.");
    }
}
