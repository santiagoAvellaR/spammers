package com.spammers.AlertsAndNotifications.model.enums;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.repository.LoanRepository;
import com.spammers.AlertsAndNotifications.service.implementations.ApiClient;
import com.spammers.AlertsAndNotifications.service.implementations.NotificationServiceImpl;
import com.spammers.AlertsAndNotifications.service.interfaces.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class NotificationServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ApiClient apiClient;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReturnBookThrowsExceptionWhenLoanNotFound() {
        String bookId = "12345";
        when(loanRepository.findLoanByBookIdAndBookReturned(bookId, false))
                .thenReturn(Optional.empty());
        SpammersPrivateExceptions exception = assertThrows(
                SpammersPrivateExceptions.class,
                () -> notificationService.returnBook(bookId, false)
        );
        assertEquals(SpammersPrivateExceptions.LOAN_NOT_FOUND, exception.getMessage());
        verify(loanRepository, times(1)).findLoanByBookIdAndBookReturned(bookId, false);
    }

}