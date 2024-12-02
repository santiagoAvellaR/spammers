package com.spammers.AlertsAndNotifications.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.spammers.AlertsAndNotifications.model.FineInputDTO;
import com.spammers.AlertsAndNotifications.model.FineModel;
import com.spammers.AlertsAndNotifications.model.dto.LoanDTO;
import com.spammers.AlertsAndNotifications.model.dto.NotificationDTO;
import com.spammers.AlertsAndNotifications.model.NotificationModel;
import com.spammers.AlertsAndNotifications.model.enums.FineType;
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

        when(notificationService.getFines(userId))
                .thenReturn(expectedFines);
        List<FineModel> actualFines = spammersController.getFines(userId);
        assertNotNull(actualFines);
        assertEquals(expectedFines, actualFines);
        verify(notificationService).getFines(userId);
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

    @Test
    void testGetNotifications() {
        // TODO After there is the DTO
    }

    @Test
    void testOpenFine(){
        String userId = "user123";
        String bookId = "book456";
        FineInputDTO fineInputDTO= new FineInputDTO((float)500.0, FineType.DAMAGE,bookId,userId);
        String output = spammersController.openFine(fineInputDTO, userId);
        assertEquals("Fine Created",output);
        verify(notificationService).openFine(fineInputDTO);
    }

    @Test
    void testCloseFine(){
        String fineId = "fine123";
        String output = spammersController.closeFine(fineId);
        assertEquals("Fine Closed",output);
        verify(notificationService).closeFine(fineId);
    }
}