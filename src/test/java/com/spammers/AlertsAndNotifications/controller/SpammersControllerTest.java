package com.spammers.AlertsAndNotifications.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.spammers.AlertsAndNotifications.model.FineModel;
import com.spammers.AlertsAndNotifications.model.dto.LoanDTO;
import com.spammers.AlertsAndNotifications.service.interfaces.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;

class SpammersControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SpammersController spammersController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /*
    @Test
    void testGetNotifications() {
        String userId = "user123";
        List<NotificationDTO> expectedNotifications = Arrays.asList(
                new NotificationDTO(),
                new NotificationDTO()
        );

        when(notificationService.getNotifications(userId))
                .thenReturn(expectedNotifications);
        List<NotificationDTO> actualNotifications = spammersController.getNotifications(userId);
        assertNotNull(actualNotifications);
        assertEquals(expectedNotifications, actualNotifications);
        verify(notificationService).getNotifications(userId);
    }
     */

    @Test
    void testGetFines() {
        String userId = "user123";
        List<FineModel> expectedFines = Arrays.asList(
                new FineModel(),
                new FineModel()
        );

        when(notificationService.getFinesByUserId(userId))
                .thenReturn(expectedFines);
        List<FineModel> actualFines = spammersController.getFines(userId);
        assertNotNull(actualFines);
        assertEquals(expectedFines, actualFines);
        verify(notificationService).getFinesByUserId(userId);
    }

    @Test
    void testNotifyLoan() {
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setUserId("user123");
        loanDTO.setBookId("book456");
        String result = spammersController.notifyLoan(loanDTO);
        assertEquals("Notification Sent!", result);
        verify(notificationService).notifyLoan(loanDTO);
    }

    @Test
    void testReturnBook_InBadCondition() {
        String bookId = "book456";
        boolean returnedInBadCondition = true;
        String result = spammersController.returnBook(bookId, returnedInBadCondition);
        assertEquals("Book Returned", result);
        verify(notificationService).returnBook(bookId, returnedInBadCondition);
    }

    @Test
    void testReturnBook_InGoodCondition() {
        String bookId = "book456";
        boolean returnedInBadCondition = false;
        String result = spammersController.returnBook(bookId, returnedInBadCondition);
        assertEquals("Book Returned", result);
        verify(notificationService).returnBook(bookId, returnedInBadCondition);
    }
}