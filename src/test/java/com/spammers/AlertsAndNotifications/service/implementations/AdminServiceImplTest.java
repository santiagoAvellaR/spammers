package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.exceptions.SpammersPublicExceptions;
import com.spammers.AlertsAndNotifications.model.FineModel;
import com.spammers.AlertsAndNotifications.model.NotificationModel;
import com.spammers.AlertsAndNotifications.model.dto.*;
import com.spammers.AlertsAndNotifications.model.LoanModel;
import com.spammers.AlertsAndNotifications.model.enums.EmailTemplate;
import com.spammers.AlertsAndNotifications.model.enums.FineDescription;
import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import com.spammers.AlertsAndNotifications.model.enums.FineType;
import com.spammers.AlertsAndNotifications.repository.FinesRepository;
import com.spammers.AlertsAndNotifications.repository.LoanRepository;
import com.spammers.AlertsAndNotifications.repository.NotificationRepository;
import com.spammers.AlertsAndNotifications.service.interfaces.AdminService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
    public class AdminServiceImplTest {

    @Mock
    private FinesRepository finesRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private EmailService emailService;
    @Mock
    private AdminService adminService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ApiClientLocal apiClient;

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
    void testReturnAllActiveFines_Success() {
        int pageSize = 10;
        int pageNumber = 0;
        List<FineModel> fines = new ArrayList<>();

        LoanModel loanModel1 = new LoanModel();
        loanModel1.setUserId("user1");
        loanModel1.setBookName("Book 1");
        loanModel1.setBookId("book1");

        FineModel fineModel1 = new FineModel();
        fineModel1.setFineId("fine1");
        fineModel1.setAmount(50.0f);
        fineModel1.setFineStatus(FineStatus.PENDING);
        fineModel1.setLoan(loanModel1);
        fines.add(fineModel1);

        LoanModel loanModel2 = new LoanModel();
        loanModel2.setUserId("user2");
        loanModel2.setBookName("Book 2");
        loanModel2.setBookId("book2");

        FineModel fineModel2 = new FineModel();
        fineModel2.setFineId("fine2");
        fineModel2.setAmount(75.0f);
        fineModel2.setFineStatus(FineStatus.PENDING);
        fineModel2.setLoan(loanModel2);
        fines.add(fineModel2);
        PageImpl<FineModel> page = new PageImpl<>(fines);
        when(finesRepository.findByStatus(eq(FineStatus.PENDING), any(Pageable.class)))
                .thenReturn(page);
        PaginatedResponseDTO<FineOutputDTO> result = adminService.returnAllActiveFines(pageSize, pageNumber);
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());
        assertEquals("fine1", result.getData().get(0).getFineId());
        assertEquals(50.0f, result.getData().get(0).getAmount());
        assertEquals("Book 1", result.getData().get(0).getBookTitle());
        assertEquals("fine2", result.getData().get(1).getFineId());
        assertEquals(75.0f, result.getData().get(1).getAmount());
        assertEquals("Book 2", result.getData().get(1).getBookTitle());
        verify(finesRepository).findByStatus(eq(FineStatus.PENDING), any(Pageable.class));
    }

    @Test
    void testReturnAllActiveFines_NoFines() {
        int pageSize = 10;
        int pageNumber = 0;
        PageImpl<FineModel> emptyPage = new PageImpl<>(Collections.emptyList());
        when(finesRepository.findByStatus(eq(FineStatus.PENDING), any(Pageable.class)))
                .thenReturn(emptyPage);
        PaginatedResponseDTO<FineOutputDTO> result = adminService.returnAllActiveFines(pageSize, pageNumber);
        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
        verify(finesRepository).findByStatus(eq(FineStatus.PENDING), any(Pageable.class));
    }

    @Test
    void testReturnAllActiveFinesBetweenDate_Success() {

        LocalDate testDate = LocalDate.of(2024, 1, 15);
        int pageSize = 10;
        int pageNumber = 0;
        LoanModel loanModel = new LoanModel();
        loanModel.setBookName("Test Book");
        loanModel.setUserId("user123");
        List<FineModel> fines = new ArrayList<>();
        FineModel fineModel = new FineModel();
        fineModel.setFineId("fine1");
        fineModel.setAmount(50.0f);
        fineModel.setFineStatus(FineStatus.PENDING);
        fineModel.setLoan(loanModel);
        fines.add(fineModel);
        PageImpl<FineModel> page = new PageImpl<>(fines);
        when(finesRepository.findByStatusAndDate(eq(FineStatus.PENDING), eq(testDate), any(Pageable.class)))
                .thenReturn(page);
        PaginatedResponseDTO<FineOutputDTO> result = adminService.returnAllActiveFinesBetweenDate(testDate, pageSize, pageNumber);
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("fine1", result.getData().get(0).getFineId());
        assertEquals(50.0f, result.getData().get(0).getAmount());
        assertEquals("Test Book", result.getData().get(0).getBookTitle());
        verify(finesRepository).findByStatusAndDate(eq(FineStatus.PENDING), eq(testDate), any(Pageable.class));
    }

    @Test
    void testReturnAllActiveFinesBetweenDate_NoFines() {
        LocalDate testDate = LocalDate.of(2024, 1, 15);
        int pageSize = 10;
        int pageNumber = 0;
        PageImpl<FineModel> emptyPage = new PageImpl<>(Collections.emptyList());
        when(finesRepository.findByStatusAndDate(eq(FineStatus.PENDING), eq(testDate), any(Pageable.class)))
                .thenReturn(emptyPage);
        PaginatedResponseDTO<FineOutputDTO> result = adminService.returnAllActiveFinesBetweenDate(testDate, pageSize, pageNumber);
        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
        verify(finesRepository).findByStatusAndDate(eq(FineStatus.PENDING), eq(testDate), any(Pageable.class));
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
        adminService.openFine(fineInputDTO);

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
        adminService.returnBook("book456", false);

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
        adminService.closeFine("fine123");

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
        adminService.returnBook("book456", true);

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
                () -> adminService.returnBook("book456", false)
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
                () -> adminService.openFine(fineInputDTO)
        );
    }

    @Test
    void testCloseFine_NotFound_ShouldThrowException() {
        // Arrange
        when(finesRepository.findById("fine123"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SpammersPrivateExceptions.class,
                () -> adminService.closeFine("fine123")
        );
    }

    @Test
    void testPendingFine_WithPendingFine() {
        // Arrange
        List<FineModel> fines = Arrays.asList(
                createFineModel(FineStatus.PENDING),
                createFineModel(FineStatus.PAID)
        );

        try {
            java.lang.reflect.Method method = AdminServiceImpl.class.getDeclaredMethod("pendingFine", List.class);
            method.setAccessible(true);

            // Act
            boolean result = (boolean) method.invoke(adminService, fines);

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
            java.lang.reflect.Method method = AdminServiceImpl.class.getDeclaredMethod("pendingFine", List.class);
            method.setAccessible(true);

            // Act
            boolean result = (boolean) method.invoke(adminService, fines);

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
            java.lang.reflect.Method method = AdminServiceImpl.class.getDeclaredMethod("daysDifference", LocalDate.class);
            method.setAccessible(true);

            // Act
            int result = (int) method.invoke(adminService, deadline);

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
            java.lang.reflect.Method method = AdminServiceImpl.class.getDeclaredMethod("daysDifference", LocalDate.class);
            method.setAccessible(true);

            // Act
            int result = (int) method.invoke(adminService, deadline);

            // Assert
            assertEquals(5, result);
        } catch (Exception e) {
            fail("Error al invocar el método privado", e);
        }
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
        adminService.notifyLoan(loanDTO);

        // Assert
        verify(loanRepository).save(any(LoanModel.class));
        verify(notificationRepository).save(any(NotificationModel.class));
        verify(emailService).sendEmailTemplate(
                eq(loanDTO.getEmailGuardian()),
                eq(EmailTemplate.NOTIFICATION_ALERT),
                anyString()
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




}