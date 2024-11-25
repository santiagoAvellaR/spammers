package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.model.LoanDTO;
import com.spammers.AlertsAndNotifications.model.LoanModel;
import com.spammers.AlertsAndNotifications.repository.FinesRepository;
import com.spammers.AlertsAndNotifications.repository.LoanRepository;
import com.spammers.AlertsAndNotifications.repository.NotificationRepository;
import com.spammers.AlertsAndNotifications.service.interfaces.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private FinesRepository finesRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmailService emailService;

    private final ApiClientTest apiClientTest=new ApiClientTest(RestClient.builder().build());

    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(
                finesRepository,
                loanRepository,
                emailService,
                notificationRepository,
                apiClientTest
        );
    }

    @Test
    void notifyLoan() {
        // Preparar datos
        LoanDTO loanDTO = new LoanDTO("", "test-1@gmail.com", "book-1", "book-test-1", LocalDate.now().plusDays(4));
        ArgumentCaptor<LoanModel> loanCaptor = ArgumentCaptor.forClass(LoanModel.class);
        notificationService.notifyLoan(loanDTO);
        verify(loanRepository).save(loanCaptor.capture());
        LoanModel savedLoan = loanCaptor.getValue();
        assertEquals("book-1", savedLoan.getBookId());
        assertEquals("book-test-1", savedLoan.getBookName());
        assertEquals(LocalDate.now().plusDays(4), savedLoan.getLoanExpired());
        assertEquals(LocalDate.now(), savedLoan.getLoanDate());
        assertTrue(savedLoan.getStatus());
        assertTrue(savedLoan.getFines().isEmpty());
    }

    @Test
    void notifySomeLoans() {
        int numberOfLoans = 10;
        ArgumentCaptor<LoanModel> loanCaptor = ArgumentCaptor.forClass(LoanModel.class);

        for(int i = 0; i < numberOfLoans; i++) {
            LoanDTO loanDTO = new LoanDTO(
                    "user_"+i,
                    "test-"+i+"@gmail.com",
                    "book-"+i,
                    "book-test-"+i,
                    LocalDate.now().plusDays(4)
            );

            notificationService.notifyLoan(loanDTO);
        }
        verify(loanRepository, times(numberOfLoans)).save(loanCaptor.capture());
        // Verify each saved loan
        List<LoanModel> savedLoans = loanCaptor.getAllValues();
        for(int i = 0; i < numberOfLoans; i++) {
            LoanModel savedLoan = savedLoans.get(i);
            assertEquals("book-"+i, savedLoan.getBookId());
            assertEquals("book-test-"+i, savedLoan.getBookName());
            assertEquals(LocalDate.now().plusDays(4), savedLoan.getLoanExpired());
            assertEquals(LocalDate.now(), savedLoan.getLoanDate());
            assertTrue(savedLoan.getStatus());
            assertTrue(savedLoan.getFines().isEmpty());
        }
    }
}