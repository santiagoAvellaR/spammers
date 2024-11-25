package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.model.LoanDTO;
import com.spammers.AlertsAndNotifications.model.LoanModel;
import com.spammers.AlertsAndNotifications.repository.FinesRepository;
import com.spammers.AlertsAndNotifications.repository.LoanRepository;
import com.spammers.AlertsAndNotifications.repository.NotificationRepository;
import com.spammers.AlertsAndNotifications.service.interfaces.EmailService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
@SpringBootTest
class NotificationServiceImplTest {

    @Autowired
    private FinesRepository finesRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    private final ApiClientTest apiClientTest = new ApiClientTest(RestClient.builder().build());

    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
    apiClientTest.deleteAllUserInfo();
    finesRepository.deleteAll();
    loanRepository.deleteAll();
    notificationRepository.deleteAll();
    notificationService =  new NotificationServiceImpl(finesRepository, loanRepository, emailService, notificationRepository, apiClientTest);
    }

    @AfterEach
    void tearDown() {
        apiClientTest.deleteAllUserInfo();
        finesRepository.deleteAll();
        loanRepository.deleteAll();
        notificationRepository.deleteAll();
    }

    @Test
    void notifyLoan() {
        LoanDTO loanDTO = new LoanDTO("","test-1@gmail.com","book-1","book-test-1", LocalDate.now().plusDays(4));
        notificationService.notifyLoan(loanDTO);
        LoanModel loan = loanRepository.findAll().get(0);
        assertEquals("book-1",loan.getBookId());
        assertEquals("book-test-1",loan.getBookName());
        assertEquals(LocalDate.now().plusDays(4),loan.getLoanExpired());
        assertEquals(LocalDate.now(),loan.getLoanDate());
        assertTrue(loan.getStatus());
        assertTrue(loan.getFines().isEmpty());
    }

    @Test
    void notifySomeLoans(){
        int numberOfLoans=10;
        for(int i = 0; i < numberOfLoans; i++) {
            LoanDTO loanDTO1 = new LoanDTO("user_"+i,"test-"+i+"@gmail.com","book-"+i,"book-test-"+i, LocalDate.now().plusDays(4));
            notificationService.notifyLoan(loanDTO1);
            LoanModel loan = loanRepository.findLoanByUserAndBookId("user_"+i,"book-"+i).get();
            assertEquals("book-"+i,loan.getBookId());
            assertEquals("book-test-"+i,loan.getBookName());
            assertEquals(LocalDate.now().plusDays(4),loan.getLoanExpired());
            assertEquals(LocalDate.now(),loan.getLoanDate());
            assertTrue(loan.getStatus());
            assertTrue(loan.getFines().isEmpty());
            System.out.println(loan.getLoanId());
            System.out.println(loan.getBookName());
            System.out.println(loan.getLoanDate());
        }
    }



    @Test
    void closeLoan() {
        //LoanDTO loanDTO = new LoanDTO("","test-1@gmail.com","book-1","book-test-1", LocalDate.now().plusDays(4));

    }

    @Test
    void getFines() {

    }

    @Test
    void getNotifications() {

    }

    @Test
    void returnBook() {

    }
}