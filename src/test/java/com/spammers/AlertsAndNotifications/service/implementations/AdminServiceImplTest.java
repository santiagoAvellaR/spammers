package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.model.FineModel;
import com.spammers.AlertsAndNotifications.model.dto.FineOutputDTO;
import com.spammers.AlertsAndNotifications.model.LoanModel;
import com.spammers.AlertsAndNotifications.model.dto.PaginatedResponseDTO;
import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import com.spammers.AlertsAndNotifications.repository.FinesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

    @ExtendWith(MockitoExtension.class)
    public class AdminServiceImplTest {

    @Mock
    private FinesRepository finesRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

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
}