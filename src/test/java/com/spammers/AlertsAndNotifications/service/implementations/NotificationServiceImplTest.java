package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.exceptions.SpammersPublicExceptions;
import com.spammers.AlertsAndNotifications.model.*;
import com.spammers.AlertsAndNotifications.model.enums.EmailTemplate;
import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import com.spammers.AlertsAndNotifications.model.enums.FineType;
import com.spammers.AlertsAndNotifications.model.enums.NotificationType;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.client.RestClient;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    @Mock
    private ApiClient apiClient;

    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(
                finesRepository,
                loanRepository,
                emailService,
                notificationRepository,
                apiClient
        );
    }

    @Test
    void notifyLoan() {
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

    @Test
    void openFine(){
        ArgumentCaptor<FineModel> fineCaptor = ArgumentCaptor.forClass(FineModel.class);
        LoanModel loan = new LoanModel("user-id-1", "book-id-1",
                LocalDate.now(), "Boulevard",
                LocalDate.now().plusDays(2), true);
        // When The method trys to find a loan, will have: Optional.of(loan)
        when(loanRepository.findByLoanId(loan.getLoanId())).thenReturn(Optional.of(loan));
        notificationService.openFine(
                loan.getLoanId(), "Time expired", 5000,
                "example@outlook.com");
        // verify the finesRepository saved the Fine
        verify(finesRepository, times(1)).save(fineCaptor.capture());
        verify(finesRepository).save(fineCaptor.capture());
        FineModel savedFine = fineCaptor.getValue();
        // Check the Fine data
        assertNotNull(savedFine);
        assertEquals("Time expired", savedFine.getDescription());
        assertEquals(5000, savedFine.getAmount());
        assertEquals(FineStatus.PENDING, savedFine.getFineStatus());
        assertEquals(loan, savedFine.getLoan());
    }


    @Test
    void openFineThrowsLoanNotFound(){
        try{
            notificationService.openFine(
                    "nonexistent-id", "Time expired", 5000,
                    "example@outlook.com");
            fail("Should throw exception because the Loan Does not exist");
        } catch(SpammersPrivateExceptions exception){
            assertEquals(SpammersPrivateExceptions.LOAN_NOT_FOUND, exception.getMessage());
        }
    }
    @Test
    void closeFine() {

        LoanModel loan = new LoanModel("user-id-1", "book-id-1",
                LocalDate.now(), "Boulevard",
                LocalDate.now().plusDays(2), true);
        FineModel fine = new FineModel("fine-1", loan, "Time expired", 8000,
                LocalDate.now().minusDays(1), FineStatus.PENDING, FineType.RETARDMENT);
        UserInfo userInfo = new UserInfo("user-1", "Guardian", "example@outlook.com");

        when(finesRepository.findById(fine.getFineId())).thenReturn(Optional.of(fine));
        when(apiClient.getUserInfoById(loan.getUserId())).thenReturn(userInfo);

        // Simulate method: finesRepository.updateFineStatus(fineId, FineStatus.PAID);
        doAnswer(invocation -> {
            String fineId = invocation.getArgument(0); // fineId
            FineStatus newStatus = invocation.getArgument(1); // fineStatus
            if (fine.getFineId().equals(fineId)) {
                fine.setFineStatus(newStatus);
            }
            return null;
        }).when(finesRepository).updateFineStatus(anyString(), any(FineStatus.class));

        notificationService.closeFine(fine.getFineId());
        verify(finesRepository, times(1)).updateFineStatus(fine.getFineId(), FineStatus.PAID);
        assertEquals(FineStatus.PAID, fine.getFineStatus());

        ArgumentCaptor<NotificationModel> notificationCaptor = ArgumentCaptor.forClass(NotificationModel.class);
        verify(notificationRepository).save(notificationCaptor.capture());
        NotificationModel savedNotification = notificationCaptor.getValue();
        assertEquals(NotificationType.FINE_PAID, savedNotification.getNotificationType());
        assertEquals("example@outlook.com", savedNotification.getEmailGuardian());
    }


    @Test
    void closeFineThrowsExceptionFineNotFound() {
        try{
            notificationService.closeFine("nonexistent-id");
            fail("Should throw exception because the Loan Does not exist");
        } catch(SpammersPrivateExceptions exception){
            assertEquals(SpammersPrivateExceptions.FINE_NOT_FOUND, exception.getMessage());
        }
    }

    @Test
    void closeLoan() {
        String userId = "user-1";
        String bookId = "book-1";
        LoanModel loan = new LoanModel(userId, bookId,
                LocalDate.now(), "Boulevard",
                LocalDate.now().plusDays(2), true);
        UserInfo userInfo = new UserInfo(userId, "Guardian", "example@outlook.com");

        // findLoanByUserAndBookId method --> return the loan
        when(loanRepository.findLoanByUserAndBookId(userId, bookId))
                .thenReturn(Optional.of(loan));

        // findByLoanId method --> return an empty list of fines
        when(finesRepository.findByLoanId(loan.getLoanId()))
                .thenReturn(Collections.emptyList());

        // getUserInfoById method --> return userInfo
        when(apiClient.getUserInfoById(userId))
                .thenReturn(userInfo);
        notificationService.closeLoan(bookId, userId);

        // Check that the loan was deleted
        verify(loanRepository, times(1)).delete(loan);

        // Check email was sent
        verify(emailService, times(1)).sendEmailTemplate(
                eq("example@outlook.com"),
                eq(EmailTemplate.NOTIFICATION_ALERT),
                contains("Devolución de libro: Boulevard")
        );

        // Check notification was saved
        ArgumentCaptor<NotificationModel> notificationCaptor = ArgumentCaptor.forClass(NotificationModel.class);
        verify(notificationRepository, times(1)).save(notificationCaptor.capture());

        NotificationModel savedNotification = notificationCaptor.getValue();
        assertEquals(userId, savedNotification.getStudentId());
        assertEquals("example@outlook.com", savedNotification.getEmailGuardian());
        assertEquals(LocalDate.now(), savedNotification.getSentDate());
        assertEquals(NotificationType.BOOK_LOAN_RETURNED, savedNotification.getNotificationType());
    }

    @Test
    void closeLoanThrowsExceptionWhenLoanNotFound() {
        String userId = "user-1";
        String bookId = "book-1";
        // findLoanByUserAndBookId method --> return empty
        when(loanRepository.findLoanByUserAndBookId(userId, bookId))
                .thenReturn(Optional.empty());

        SpammersPrivateExceptions exception = assertThrows(
                SpammersPrivateExceptions.class,() -> notificationService.closeLoan(bookId, userId));

        assertEquals(SpammersPrivateExceptions.LOAN_NOT_FOUND, exception.getMessage());
        verify(loanRepository, never()).delete(any());
    }

    @Test
    void closeLoanThrowsExceptionWhenPendingFinesExist() {
        String userId = "user-1";
        String bookId = "book-1";
        LoanModel loan = new LoanModel(userId, bookId,
                LocalDate.now(), "Boulevard",
                LocalDate.now().plusDays(2), true);

        FineModel pendingFine = new FineModel("fine-1", loan, "Time expired", 8000,
                LocalDate.now().minusDays(1), FineStatus.PENDING, FineType.RETARDMENT);

        //findLoanByUserAndBookId method --> return the loan
        when(loanRepository.findLoanByUserAndBookId(userId, bookId))
                .thenReturn(Optional.of(loan));

        //findByLoanId method --> return a pending fine
        when(finesRepository.findByLoanId(loan.getLoanId()))
                .thenReturn(Collections.singletonList(pendingFine));
        SpammersPublicExceptions exception = assertThrows(
                SpammersPublicExceptions.class,() -> notificationService.closeLoan(bookId, userId));
        assertEquals(SpammersPublicExceptions.FINE_PENDING, exception.getMessage());
        verify(loanRepository, never()).delete(any());
    }

    @Test
    void getFines() {
        // Arrange
        String userId = "user-1";
        int pageSize = 15;
        int pageNumber = 0;

        // Crear préstamos con multas
        LoanModel loan1 = new LoanModel("user-1", "book-1",
                LocalDate.now().minusDays(10), "Book 1",
                LocalDate.now().minusDays(5), true);

        LoanModel loan2 = new LoanModel("user-1", "book-2",
                LocalDate.now().minusDays(15), "Book 2",
                LocalDate.now().minusDays(8), true);

        // Crear multas para los préstamos
        FineModel fine1 = new FineModel("fine-1", loan1, "Late return", 5000f,
                LocalDate.now().minusDays(5), FineStatus.PENDING, FineType.RETARDMENT);

        FineModel fine2 = new FineModel("fine-2", loan2, "Damaged book", 8000f,
                LocalDate.now().minusDays(8), FineStatus.PENDING, FineType.DAMAGE);

        loan1.setFines(Collections.singletonList(fine1));
        loan2.setFines(Collections.singletonList(fine2));

        // Crear una página de préstamos
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<LoanModel> loanPage = new PageImpl<>(Arrays.asList(loan1, loan2), pageRequest, 2);

        // Mock the findByUserId method to return the page of loans
        when(loanRepository.findByUserId(userId, pageRequest))
                .thenReturn(loanPage);

        // Act
        List<FineModel> resultFines = notificationService.getFines(userId);

        // Assert
        // Verify the correct number of fines are returned
        assertEquals(2, resultFines.size());

        // Verify the details of the returned fines
        assertTrue(resultFines.contains(fine1));
        assertTrue(resultFines.contains(fine2));

        // Verify that the repository method was called with correct parameters
        verify(loanRepository, times(1)).findByUserId(userId, pageRequest);
    }

    @Test
    void getFinesWithMultiplePages() {
        // Arrange
        String userId = "user-1";
        int pageSize = 15;

        // Crear préstamos con multas para múltiples páginas
        List<LoanModel> loansPage1 = new ArrayList<>();
        List<LoanModel> loansPage2 = new ArrayList<>();

        // Crear préstamos y multas para la primera página
        for (int i = 0; i < 15; i++) {
            LoanModel loan = new LoanModel("user-1", "book-" + i,
                    LocalDate.now().minusDays(10 + i), "Book " + i,
                    LocalDate.now().minusDays(5 + i), true);

            FineModel fine = new FineModel("fine-" + i, loan, "Late return", 5000f + i,
                    LocalDate.now().minusDays(5 + i), FineStatus.PENDING, FineType.RETARDMENT);

            loan.setFines(Collections.singletonList(fine));
            loansPage1.add(loan);
        }

        // Crear préstamos y multas para la segunda página
        for (int i = 0; i < 10; i++) {
            LoanModel loan = new LoanModel("user-1", "book-" + (i + 15),
                    LocalDate.now().minusDays(25 + i), "Book " + (i + 15),
                    LocalDate.now().minusDays(20 + i), true);

            FineModel fine = new FineModel("fine-" + (i + 15), loan, "Damaged book", 8000f + i,
                    LocalDate.now().minusDays(20 + i), FineStatus.PENDING, FineType.DAMAGE);

            loan.setFines(Collections.singletonList(fine));
            loansPage2.add(loan);
        }

        // Simular páginas
        PageRequest pageRequest1 = PageRequest.of(0, pageSize);
        PageRequest pageRequest2 = PageRequest.of(1, pageSize);

        Page<LoanModel> loanPage1 = new PageImpl<>(loansPage1, pageRequest1, 25);
        Page<LoanModel> loanPage2 = new PageImpl<>(loansPage2, pageRequest2, 25);

        // Mock the findByUserId method to return pages of loans
        when(loanRepository.findByUserId(userId, pageRequest1))
                .thenReturn(loanPage1);
        when(loanRepository.findByUserId(userId, pageRequest2))
                .thenReturn(loanPage2);

        // Act
        List<FineModel> resultFines = notificationService.getFines(userId);

        // Assert
        // Verificar que se recuperaron todas las multas de ambas páginas
        assertEquals(25, resultFines.size());

        // Verificar que todas las multas de la primera página están en el resultado
        assertTrue(resultFines.stream().anyMatch(fine -> fine.getFineId().equals("fine-0")));
        assertTrue(resultFines.stream().anyMatch(fine -> fine.getFineId().equals("fine-14")));

        // Verificar que todas las multas de la segunda página están en el resultado
        assertTrue(resultFines.stream().anyMatch(fine -> fine.getFineId().equals("fine-15")));
        assertTrue(resultFines.stream().anyMatch(fine -> fine.getFineId().equals("fine-24")));

        // Verificar que se llamó al método del repositorio para ambas páginas
        verify(loanRepository, times(1)).findByUserId(userId, pageRequest1);
        verify(loanRepository, times(1)).findByUserId(userId, pageRequest2);
    }

    @Test
    void getFinesNoFines() {
        // Arrange
        String userId = "user-1";
        int pageSize = 15;
        int pageNumber = 0;

        // Crear una página de préstamos sin multas
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        LoanModel loan1 = new LoanModel("user-1", "book-1",
                LocalDate.now().minusDays(10), "Book 1",
                LocalDate.now().minusDays(5), true);
        loan1.setFines(Collections.emptyList());

        LoanModel loan2 = new LoanModel("user-1", "book-2",
                LocalDate.now().minusDays(15), "Book 2",
                LocalDate.now().minusDays(8), true);
        loan2.setFines(Collections.emptyList());

        Page<LoanModel> loanPage = new PageImpl<>(Arrays.asList(loan1, loan2), pageRequest, 2);

        // Mock the findByUserId method to return the page of loans
        when(loanRepository.findByUserId(userId, pageRequest))
                .thenReturn(loanPage);

        // Act
        List<FineModel> resultFines = notificationService.getFines(userId);

        // Assert
        // Verificar que no se devuelven multas
        assertTrue(resultFines.isEmpty());

        // Verificar que se llamó al método del repositorio
        verify(loanRepository, times(1)).findByUserId(userId, pageRequest);
    }

    @Test
    void getNotifications(){

    }

}