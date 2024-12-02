package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.model.LoanModel;
import com.spammers.AlertsAndNotifications.model.NotificationModel;
import com.spammers.AlertsAndNotifications.model.dto.UserInfo;
import com.spammers.AlertsAndNotifications.model.enums.EmailTemplate;
import com.spammers.AlertsAndNotifications.model.enums.NotificationType;
import com.spammers.AlertsAndNotifications.repository.LoanRepository;
import com.spammers.AlertsAndNotifications.repository.NotificationRepository;
import com.spammers.AlertsAndNotifications.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LoanThreeDaysBfReturnAlert {

    private final LoanRepository loanRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final ApiClient apiClient;
    private int page = 0;
    private final int EXECUTIONS = 15;
    @Scheduled(cron = "0 */10 11-13 * * *")
    private void checkLoans(){
        processEmails();
        page++;
        //Current time
        LocalTime now = LocalTime.now();
        // Define the time 10:50am
        LocalTime comparisonTime = LocalTime.of(13, 50);
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
        Pageable pageable = PageRequest.of(page, EXECUTIONS);
        return loanRepository.findLoansExpiringInExactlyNDays(LocalDate.now().plusDays(3), pageable);
    }

    private void sendEmail(LoanModel loan) {
        UserInfo userInfo = apiClient.getUserInfoById(loan.getUserId());
        emailService.sendEmailTemplate(userInfo.getGuardianEmail(), EmailTemplate.NOTIFICATION_ALERT
                ,"Estudiante: " + userInfo.getName() + "tiene 3 dias para devolver el libro, de lo contrario se generará una multa.");
        NotificationModel notificationModel = new NotificationModel(loan.getUserId(),userInfo.getGuardianEmail()
                , LocalDate.now() , NotificationType.ALERT, false, loan.getBookName());
        notificationRepository.save(notificationModel);
    }
}
