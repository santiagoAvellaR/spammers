package com.spammers.AlertsAndNotifications.service.implementations;
import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.exceptions.SpammersPublicExceptions;
import com.spammers.AlertsAndNotifications.model.*;
import com.spammers.AlertsAndNotifications.model.dto.*;
import com.spammers.AlertsAndNotifications.model.enums.*;
import com.spammers.AlertsAndNotifications.repository.FinesRepository;
import com.spammers.AlertsAndNotifications.repository.LoanRepository;
import com.spammers.AlertsAndNotifications.repository.NotificationRepository;
import com.spammers.AlertsAndNotifications.service.interfaces.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private FinesRepository finesRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ApiClientLocal apiClient;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private LoanDTO loanDTO;
    private UserInfo userInfo;
    private LoanModel loanModel;

    @BeforeEach
    void setUp() {
        loanDTO = new LoanDTO();
        loanDTO.setUserId("user123");
        loanDTO.setBookId("book456");
        loanDTO.setBookName("Test Book");
        loanDTO.setEmailGuardian("guardian@email.com");
        loanDTO.setLoanReturn(LocalDate.now().plusDays(14));

        userInfo = new UserInfo();
        userInfo.setName("Test User");
        userInfo.setGuardianEmail("guardian@email.com");

        loanModel = new LoanModel();
        loanModel.setUserId("user123");
        loanModel.setBookId("book456");
        loanModel.setLoanDate(LocalDate.now());
        loanModel.setBookName("Test Book");
        loanModel.setLoanExpired(LocalDate.now().plusDays(14));
        loanModel.setBookReturned(false);
    }

    @Test
    void testNotifyLoan() throws SpammersPublicExceptions, SpammersPrivateExceptions {
        // Arrange
        when(loanRepository.save(any(LoanModel.class))).thenReturn(loanModel);
        when(notificationRepository.save(any(NotificationModel.class))).thenReturn(null);
        doNothing().when(emailService).sendEmailTemplate(
                anyString(),
                any(EmailTemplate.class),
                anyString()
        );

        // Act
        notificationService.notifyLoan(loanDTO);

        // Assert
        verify(loanRepository).save(any(LoanModel.class));
        verify(notificationRepository).save(any(NotificationModel.class));
        verify(emailService).sendEmailTemplate(
                eq(loanDTO.getEmailGuardian()),
                eq(EmailTemplate.NOTIFICATION_ALERT),
                anyString()
        );
    }

    @Test
    void testGetNotifications_Success() {
        // Arrange
        String userId = "user123";
        int pageNumber = 0;
        int pageSize = 10;

        List<NotificationModel> notifications = new ArrayList<>();
        NotificationModel notificationModel = new NotificationModel(userId,"example@mail.com",
                LocalDate.now().minusDays(2),NotificationType.FINE,false,"Boulevard");
        notifications.add(notificationModel);
        PageImpl<NotificationModel> page = new PageImpl<>(notifications);
        when(notificationRepository.findByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(page);

        // Act
        PaginatedResponseDTO<NotificationDTO> result = notificationService.getNotifications(userId, pageNumber, pageSize);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("Boulevard", result.getData().get(0).getBookName());

        verify(notificationRepository).findByUserId(eq(userId), any(Pageable.class));
    }

    @Test
    void testGetNotifications_NoNotifications() {
        // Arrange
        String userId = "user123";
        int pageNumber = 0;
        int pageSize = 10;

        PageImpl<NotificationModel> emptyPage = new PageImpl<>(Collections.emptyList());

        when(notificationRepository.findByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(emptyPage);

        // Act
        PaginatedResponseDTO<NotificationDTO> result = notificationService.getNotifications(userId, pageNumber, pageSize);

        // Assert
        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
        verify(notificationRepository).findByUserId(eq(userId), any(Pageable.class));
    }

    @Test
    void testOpenFine_Success() throws SpammersPublicExceptions, SpammersPrivateExceptions {
        // Arrange
        FineInputDTO fineInputDTO = new FineInputDTO();
        fineInputDTO.setAmount(50.0f);  // Use float instead of double
        fineInputDTO.setFineType(FineType.DAMAGE);
        fineInputDTO.setBookId("book456");
        fineInputDTO.setUserId("user123");

        when(loanRepository.findLastLoan("book456", "user123"))
                .thenReturn(Optional.of(loanModel));
        when(finesRepository.save(any(FineModel.class))).thenReturn(null);

        // Specific stubbing for emailService to match exact parameters
        doNothing().when(emailService).sendEmailTemplate(
                eq(userInfo.getGuardianEmail()),
                eq(EmailTemplate.FINE_ALERT),
                eq("Se ha registrado una nueva multa: "),
                eq(50.0f),  // Use float here
                any(LocalDate.class),
                eq(FineDescription.DAMAGED_MATERIAL.getDescription())
        );

        // Act
        notificationService.openFine(fineInputDTO);

        // Assert
        verify(finesRepository).save(any(FineModel.class));
        verify(notificationRepository).save(any(NotificationModel.class));
        verify(emailService).sendEmailTemplate(
                eq(userInfo.getGuardianEmail()),
                eq(EmailTemplate.FINE_ALERT),
                eq("Se ha registrado una nueva multa: "),
                eq(50.0f),
                any(LocalDate.class),
                eq(FineDescription.DAMAGED_MATERIAL.getDescription())
        );
    }

    @Test
    void testGetFines_Success() {
        // Arrange
        String userId = "user123";
        int size = 15;
        int pageNumber = 0;

        List<FineModel> fines = new ArrayList<>();
        FineModel fineModel = new FineModel();
        fineModel.setLoan(loanModel);
        fineModel.setFineId("fine123");
        fineModel.setAmount(50.0f);
        fineModel.setDescription("Test Fine");
        fines.add(fineModel);

        PageImpl<FineModel> page = new PageImpl<>(fines);

        when(finesRepository.findByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(page);
        // Act
        PaginatedResponseDTO<FineOutputDTO> result = notificationService.getFinesByUserId(userId, size, pageNumber);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());

        verify(finesRepository).findByUserId(eq(userId), any(Pageable.class));
    }

    @Test
    void testReturnBook_Success() {
        // Arrange
        when(loanRepository.findLoanByBookIdAndBookReturned("book456", false))
                .thenReturn(Optional.of(loanModel));
        doNothing().when(emailService).sendEmailCustomised(
                anyString(),
                anyString(),
                anyString()
        );

        // Act
        notificationService.returnBook("book456", false);

        // Assert
        verify(loanRepository).save(loanModel);
        verify(emailService).sendEmailCustomised(
                eq(userInfo.getGuardianEmail()),
                eq("Devolución de un libro"),
                anyString()
        );
        assertTrue(loanModel.isBookReturned());
    }

    @Test
    void testCloseFine_Success() throws SpammersPrivateExceptions {
        // Arrange
        FineModel fineModel = new FineModel();
        fineModel.setLoan(loanModel);
        fineModel.setAmount(50.0f);  // Note the float type
        fineModel.setDescription("Test Fine");
        fineModel.setFineId("fine123");

        when(finesRepository.findById("fine123"))
                .thenReturn(Optional.of(fineModel));

        // Updated stubbing to match the actual method call
        doNothing().when(emailService).sendEmailTemplate(
                eq(userInfo.getGuardianEmail()),
                eq(EmailTemplate.FINE_ALERT),
                anyString(),
                eq(50.0f),  // Use float instead of double
                any(LocalDate.class),
                anyString()
        );

        // Act
        notificationService.closeFine("fine123");

        // Assert
        verify(finesRepository).updateFineStatus("fine123", FineStatus.PAID);
        verify(notificationRepository).save(any(NotificationModel.class));
        verify(emailService).sendEmailTemplate(
                eq(userInfo.getGuardianEmail()),
                eq(EmailTemplate.FINE_ALERT),
                anyString(),
                eq(50.0f),  // Use float here as well
                any(LocalDate.class),
                eq("Test Fine")
        );
    }
    @Test
    void testReturnBook_BadCondition() {
        // Arrange
        when(loanRepository.findLoanByBookIdAndBookReturned("book456", false))
                .thenReturn(Optional.of(loanModel));
        doNothing().when(emailService).sendEmailCustomised(
                anyString(),
                anyString(),
                anyString()
        );

        // Act
        notificationService.returnBook("book456", true);

        // Assert
        verify(loanRepository).save(loanModel);
        verify(emailService).sendEmailCustomised(
                eq(userInfo.getGuardianEmail()),
                eq("Devolución de un libro"),
                anyString()
        );
        assertTrue(loanModel.isBookReturned());
    }

    @Test
    void testReturnBook_LoanNotFound() {
        // Arrange
        when(loanRepository.findLoanByBookIdAndBookReturned("book456", false))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SpammersPrivateExceptions.class,
                () -> notificationService.returnBook("book456", false)
        );
    }

    @Test
    void testOpenFine_NoLastLoan_ShouldThrowException() {
        // Arrange
        FineInputDTO fineInputDTO = new FineInputDTO();
        fineInputDTO.setUserId("user123");
        fineInputDTO.setBookId("book456");
        fineInputDTO.setFineType(FineType.RETARDMENT);
        fineInputDTO.setAmount((float)25.0);

        when(loanRepository.findLastLoan("book456", "user123"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SpammersPrivateExceptions.class,
                () -> notificationService.openFine(fineInputDTO)
        );
    }

    @Test
    void testCloseFine_NotFound_ShouldThrowException() {
        // Arrange
        when(finesRepository.findById("fine123"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SpammersPrivateExceptions.class,
                () -> notificationService.closeFine("fine123")
        );
    }



    private FineModel createFineModel(FineStatus status) {
        FineModel fineModel = new FineModel();
        fineModel.setFineId(UUID.randomUUID().toString());
        fineModel.setFineStatus(status);
        fineModel.setAmount((float)50.0);
        fineModel.setDescription("Test Fine");
        fineModel.setExpiredDate(LocalDate.now());
        fineModel.setFineType(FineType.RETARDMENT);
        fineModel.setLoan(loanModel);
        return fineModel;
    }

    @Test
    void testPendingFine_WithPendingFine() {
        // Arrange
        List<FineModel> fines = Arrays.asList(
                createFineModel(FineStatus.PENDING),
                createFineModel(FineStatus.PAID)
        );

        try {
            java.lang.reflect.Method method = NotificationServiceImpl.class.getDeclaredMethod("pendingFine", List.class);
            method.setAccessible(true);

            // Act
            boolean result = (boolean) method.invoke(notificationService, fines);

            // Assert
            assertTrue(result);
        } catch (Exception e) {
            fail("Error al invocar el método privado", e);
        }
    }

    @Test
    void testPendingFine_WithoutPendingFine() {
        // Arrange
        List<FineModel> fines = Arrays.asList(
                createFineModel(FineStatus.PAID),
                createFineModel(FineStatus.PAID)
        );

        try {
            java.lang.reflect.Method method = NotificationServiceImpl.class.getDeclaredMethod("pendingFine", List.class);
            method.setAccessible(true);

            // Act
            boolean result = (boolean) method.invoke(notificationService, fines);

            // Assert
            assertFalse(result);
        } catch (Exception e) {
            fail("Error al invocar el método privado", e);
        }
    }

    @Test
    void testDaysDifference_BeforeDeadline() {
        // Arrange
        LocalDate deadline = LocalDate.now().plusDays(5);

        try {
            java.lang.reflect.Method method = NotificationServiceImpl.class.getDeclaredMethod("daysDifference", LocalDate.class);
            method.setAccessible(true);

            // Act
            int result = (int) method.invoke(notificationService, deadline);

            // Assert
            assertEquals(0, result);
        } catch (Exception e) {
            fail("Error al invocar el método privado", e);
        }
    }

    @Test
    void testDaysDifference_AfterDeadline() {
        // Arrange
        LocalDate deadline = LocalDate.now().minusDays(5);

        try {
            java.lang.reflect.Method method = NotificationServiceImpl.class.getDeclaredMethod("daysDifference", LocalDate.class);
            method.setAccessible(true);

            // Act
            int result = (int) method.invoke(notificationService, deadline);

            // Assert
            assertEquals(5, result);
        } catch (Exception e) {
            fail("Error al invocar el método privado", e);
        }
    }
}