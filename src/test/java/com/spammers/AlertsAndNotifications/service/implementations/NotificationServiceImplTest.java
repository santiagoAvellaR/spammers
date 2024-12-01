package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.exceptions.SpammersPublicExceptions;
import com.spammers.AlertsAndNotifications.model.*;
import com.spammers.AlertsAndNotifications.model.dto.LoanDTO;
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

import java.lang.reflect.Method;
import java.time.LocalDate;
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
                notificationRepository
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
    void returnBookThrowsExceptionWhenLoanNotFound() {
        String bookId = "book-1";
        //findLoanByBookIdAndBookReturned method --> return empty
        when(loanRepository.findLoanByBookIdAndBookReturned(bookId, false))
                .thenReturn(Optional.empty());
        SpammersPrivateExceptions exception = assertThrows(
                SpammersPrivateExceptions.class, () -> notificationService.returnBook(bookId, false));

        assertEquals(SpammersPrivateExceptions.LOAN_NOT_FOUND, exception.getMessage());
        verify(loanRepository, never()).save(any());
        verify(emailService, never()).sendEmailCustomised(any(), any(), any());
    }
    /*
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

     */

    /*
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
    */



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
                LocalDate.now().minusDays(1), FineStatus.PENDING, FineType.RETARDMENT,List.of());

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
        String userId = "user-1";
        int pageSize = 15;
        int pageNumber = 0;

        // Create some loans
        LoanModel loan1 = new LoanModel("user-1", "book-1",
                LocalDate.now().minusDays(10), "Book 1",
                LocalDate.now().minusDays(5), true);

        LoanModel loan2 = new LoanModel("user-1", "book-2",
                LocalDate.now().minusDays(15), "Book 2",
                LocalDate.now().minusDays(8), true);

        // create some fines
        FineModel fine1 = new FineModel("fine-1", loan1, "Late return", 5000f,
                LocalDate.now().minusDays(5), FineStatus.PENDING, FineType.RETARDMENT,List.of());

        FineModel fine2 = new FineModel("fine-2", loan2, "Damaged book", 8000f,
                LocalDate.now().minusDays(8), FineStatus.PENDING, FineType.DAMAGE,List.of());

        loan1.setFines(Collections.singletonList(fine1));
        loan2.setFines(Collections.singletonList(fine2));

        // Create a page for loans:
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<LoanModel> loanPage = new PageImpl<>(Arrays.asList(loan1, loan2), pageRequest, 2);

        // findByUserId method --> return the page of loans
        when(loanRepository.findByUserId(userId, pageRequest))
                .thenReturn(loanPage);
        List<FineModel> resultFines = notificationService.getFines(userId);

        // Check the correct number of fines are returned
        assertEquals(2, resultFines.size());

        // Check data of the fines
        assertTrue(resultFines.contains(fine1));
        assertTrue(resultFines.contains(fine2));

        // Check the repository method was called with correct parameters
        verify(loanRepository, times(1)).findByUserId(userId, pageRequest);
    }

    @Test
    void getFinesWithMultiplePages() {
        String userId = "user-1";
        int pageSize = 15;
        List<LoanModel> loansPage1 = new ArrayList<>();
        List<LoanModel> loansPage2 = new ArrayList<>();

        // Create loans and fines first page (15 rows)
        for (int i = 0; i < 15; i++) {
            LoanModel loan = new LoanModel("user-1", "book-" + i,
                    LocalDate.now().minusDays(10 + i), "Book " + i,
                    LocalDate.now().minusDays(5 + i), true);

            FineModel fine = new FineModel("fine-" + i, loan, "Late return", 5000f + i,
                    LocalDate.now().minusDays(5 + i), FineStatus.PENDING, FineType.RETARDMENT,List.of());

            loan.setFines(Collections.singletonList(fine));
            loansPage1.add(loan);
        }

        // Create loans second page (10 rows)
        for (int i = 0; i < 10; i++) {
            LoanModel loan = new LoanModel("user-1", "book-" + (i + 15),
                    LocalDate.now().minusDays(25 + i), "Book " + (i + 15),
                    LocalDate.now().minusDays(20 + i), true);

            FineModel fine = new FineModel("fine-" + (i + 15), loan, "Damaged book", 8000f + i,
                    LocalDate.now().minusDays(20 + i), FineStatus.PENDING, FineType.DAMAGE,List.of());

            loan.setFines(Collections.singletonList(fine));
            loansPage2.add(loan);
        }
        // Simulate pages
        PageRequest pageRequest1 = PageRequest.of(0, pageSize);
        PageRequest pageRequest2 = PageRequest.of(1, pageSize);

        Page<LoanModel> loanPage1 = new PageImpl<>(loansPage1, pageRequest1, 25);
        Page<LoanModel> loanPage2 = new PageImpl<>(loansPage2, pageRequest2, 25);

        // findByUserId method --> return pages of loans
        when(loanRepository.findByUserId(userId, pageRequest1))
                .thenReturn(loanPage1);
        when(loanRepository.findByUserId(userId, pageRequest2))
                .thenReturn(loanPage2);
        List<FineModel> resultFines = notificationService.getFines(userId);

        // Check all fines returned from the pages.
        assertEquals(25, resultFines.size());

        // Check all fines from first page
        assertTrue(resultFines.stream().anyMatch(fine -> fine.getFineId().equals("fine-0")));
        assertTrue(resultFines.stream().anyMatch(fine -> fine.getFineId().equals("fine-14")));

        // Check all fines from second page
        assertTrue(resultFines.stream().anyMatch(fine -> fine.getFineId().equals("fine-15")));
        assertTrue(resultFines.stream().anyMatch(fine -> fine.getFineId().equals("fine-24")));

        // check the method was called just one time per page.
        verify(loanRepository, times(1)).findByUserId(userId, pageRequest1);
        verify(loanRepository, times(1)).findByUserId(userId, pageRequest2);
    }

    @Test
    void getFinesNoFines() {
        String userId = "user-1";
        int pageSize = 15;
        int pageNumber = 0;
        // Check page with no fines
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        LoanModel loan1 = new LoanModel("user-1", "book-1",
                LocalDate.now().minusDays(10), "Book 1", LocalDate.now().minusDays(5), true);
        loan1.setFines(Collections.emptyList());

        LoanModel loan2 = new LoanModel("user-1", "book-2",
                LocalDate.now().minusDays(15), "Book 2", LocalDate.now().minusDays(8), true);
        loan2.setFines(Collections.emptyList());

        Page<LoanModel> loanPage = new PageImpl<>(Arrays.asList(loan1, loan2), pageRequest, 2);

        // findByUserId method --> return the page of loans
        when(loanRepository.findByUserId(userId, pageRequest))
                .thenReturn(loanPage);
        List<FineModel> resultFines = notificationService.getFines(userId);

        // Check there are not fines
        assertTrue(resultFines.isEmpty());

        // Check the repository was called just one time cause, there were no fines.
        verify(loanRepository, times(1)).findByUserId(userId, pageRequest);
    }

    /*
    @Test
    void getNotificationsWithMultiplePages() {
        String userId = "user-1";
        int pageSize = 15;
        List<NotificationModel> mockNotifications = createMockNotifications(userId, 35);
        Page<NotificationModel> firstPage = new PageImpl<>(
                mockNotifications.subList(0, pageSize),
                PageRequest.of(0, pageSize),
                mockNotifications.size()
        );

        Page<NotificationModel> secondPage = new PageImpl<>(
                mockNotifications.subList(pageSize, 2 * pageSize),
                PageRequest.of(1, pageSize),
                mockNotifications.size()
        );

        Page<NotificationModel> thirdPage = new PageImpl<>(
                mockNotifications.subList(2 * pageSize, mockNotifications.size()),
                PageRequest.of(2, pageSize),
                mockNotifications.size()
        );

        // findByUser --> return respective page, because there are 35 notifications (3 pages)
        when(notificationRepository.findByUserId(eq(userId), eq(PageRequest.of(0, pageSize))))
                .thenReturn(firstPage);
        when(notificationRepository.findByUserId(eq(userId), eq(PageRequest.of(1, pageSize))))
                .thenReturn(secondPage);
        when(notificationRepository.findByUserId(eq(userId), eq(PageRequest.of(2, pageSize))))
                .thenReturn(thirdPage);

        List<NotificationDTO> retrievedNotifications = notificationService.getNotifications(userId);

        // Check the number of all notifications
        assertEquals(mockNotifications.size(), retrievedNotifications.size());

        // Check calls to repository (3 times because 3 pages)
        verify(notificationRepository, times(3)).findByUserId(eq(userId), any(PageRequest.class));
    }
     */

    /*
    @Test
    void getNotificationsWithNoNotifications() {
        String userId = "user-1";
        int pageSize = 15;
        Page<NotificationModel> emptyPage = Page.empty();
        // findByUserId --> return emptyPage (because there are no notifications)
        when(notificationRepository.findByUserId(eq(userId), any(PageRequest.class)))
                .thenReturn(emptyPage);
        List<NotificationDTO> retrievedNotifications = notificationService.getNotifications(userId);
        assertTrue(retrievedNotifications.isEmpty());

        // Check calls to repository (the minimum calls, 1 because there are less than 15 notifications)
        verify(notificationRepository, times(1)).findByUserId(eq(userId), eq(PageRequest.of(0, pageSize)));
    }
     */

    private List<NotificationModel> createMockNotifications(String userId) {
        return createMockNotifications(userId, 10);
    }

    private List<NotificationModel> createMockNotifications(String userId, int count) {
        List<NotificationModel> notifications = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            NotificationModel notification = new NotificationModel(
                    userId,
                    "email-" + i + "@example.com",
                    LocalDate.now().minusDays(i),
                    NotificationType.values()[i % NotificationType.values().length]
            );
            notifications.add(notification);
        }
        return notifications;
    }


    private <T> T invokePrivateMethod(Object obj, String methodName, Class<?> paramType, Object... args) {
        try {
            Method method = obj.getClass().getDeclaredMethod(methodName, paramType);
            method.setAccessible(true);
            return (T) method.invoke(obj, args);
        } catch (Exception e) {
            throw new RuntimeException("Error invoking private method", e);
        }
    }

    // Overloaded method to handle multiple parameter types
    private <T> T invokePrivateMethod(Object obj, String methodName, Class<?>[] paramTypes, Object... args) {
        try {
            Method method = obj.getClass().getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            return (T) method.invoke(obj, args);
        } catch (Exception e) {
            throw new RuntimeException("Error invoking private method", e);
        }
    }
}