package com.spammers.AlertsAndNotifications.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spammers.AlertsAndNotifications.model.dto.FineInputDTO;
import com.spammers.AlertsAndNotifications.model.dto.FineOutputDTO;
import com.spammers.AlertsAndNotifications.model.dto.LoanDTO;
import com.spammers.AlertsAndNotifications.model.dto.PaginatedResponseDTO;
import com.spammers.AlertsAndNotifications.service.interfaces.AdminService;
import com.spammers.AlertsAndNotifications.service.interfaces.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void testGetPendingFinesByDate_Success() throws Exception {
        LocalDate testDate = LocalDate.of(2024, 1, 15);
        int page = 0;
        int size = 10;

        List<FineOutputDTO> fines = new ArrayList<>();
        FineOutputDTO fineDto = new FineOutputDTO();
        fineDto.setFineId("fine1");
        fineDto.setAmount(50.0f);
        fineDto.setBookTitle("Test Book");
        fines.add(fineDto);

        PaginatedResponseDTO<FineOutputDTO> responseDTO = new PaginatedResponseDTO<>(
                fines, page, 1, 1
        );

        when(adminService.returnAllActiveFinesBetweenDate(eq(testDate), eq(size), eq(page)))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/notifications/admin/loans-about-expire")
                        .param("date", testDate.toString())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].fineId").value("fine1"))
                .andExpect(jsonPath("$.data[0].amount").value(50.0));

        verify(adminService).returnAllActiveFinesBetweenDate(eq(testDate), eq(size), eq(page));
    }

    @Test
    void testGetPendingFinesByDate_NoFines() throws Exception {
        LocalDate testDate = LocalDate.of(2024, 1, 15);
        int page = 0;
        int size = 10;

        PaginatedResponseDTO<FineOutputDTO> responseDTO = new PaginatedResponseDTO<>(
                Collections.emptyList(), page, 0, 0
        );

        when(adminService.returnAllActiveFinesBetweenDate(eq(testDate), eq(size), eq(page)))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/notifications/admin/loans-about-expire")
                        .param("date", testDate.toString())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(adminService).returnAllActiveFinesBetweenDate(eq(testDate), eq(size), eq(page));
    }

    @Test
    void testGetPendingFines_Success() throws Exception {
        int page = 0;
        int size = 10;

        List<FineOutputDTO> fines = new ArrayList<>();
        FineOutputDTO fineDto = new FineOutputDTO();
        fineDto.setFineId("fine1");
        fineDto.setAmount(50.0f);
        fineDto.setBookTitle("Test Book");
        fines.add(fineDto);

        PaginatedResponseDTO<FineOutputDTO> responseDTO = new PaginatedResponseDTO<>(
                fines, page, 1, 1
        );

        when(adminService.returnAllActiveFines(eq(page), eq(size)))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/notifications/admin/fines-pending")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].fineId").value("fine1"))
                .andExpect(jsonPath("$.data[0].amount").value(50.0));

        verify(adminService).returnAllActiveFines(eq(page), eq(size));
    }

    @Test
    void testGetPendingFines_NoFines() throws Exception {
        int page = 0;
        int size = 10;

        PaginatedResponseDTO<FineOutputDTO> responseDTO = new PaginatedResponseDTO<>(
                Collections.emptyList(), page, 0, 0
        );

        when(adminService.returnAllActiveFines(eq(page), eq(size)))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/notifications/admin/fines-pending")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(adminService).returnAllActiveFines(eq(page), eq(size));
    }
    @Test
    void testNotifyLoan_Success() throws Exception {
        // Arrange
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setUserId("user123");
        loanDTO.setBookId("book456");

        // Act & Assert
        mockMvc.perform(post("/notifications/admin/notify-create-loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loanDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification Sent!"));

        // Verify service method was called
        verify(adminService).notifyLoan(any(LoanDTO.class));
    }
    @Test
    void testReturnBook_Success() throws Exception {
        // Arrange
        String bookId = "book123";
        boolean returnedInBadCondition = false;

        // Act & Assert
        mockMvc.perform(post("/notifications/admin/notify-return-loan")
                        .param("bookId", bookId)
                        .param("returnedInBadCondition", String.valueOf(returnedInBadCondition)))
                .andExpect(status().isOk())
                .andExpect(content().string("Book Returned"));

        // Verify service method was called
        verify(adminService).returnBook(eq(bookId), eq(returnedInBadCondition));
    }


    @Test
    void testOpenFine_Success() throws Exception {
        // Arrange
        String userId = "user123";
        FineInputDTO fineDTO = new FineInputDTO();
        fineDTO.setAmount(50.0f);

        // Act & Assert
        mockMvc.perform(post("/notifications/admin/users/{userId}/fines/create", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(fineDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Fine Created"));

        // Verify service method was called
        verify(adminService).openFine(any(FineInputDTO.class));
    }

    @Test
    void testCloseFine_Success() throws Exception {
        // Arrange
        String fineId = "fine123";

        // Act & Assert
        mockMvc.perform(put("/notifications/admin/users/fines/{fineId}/close", fineId))
                .andExpect(status().isOk())
                .andExpect(content().string("Fine Closed"));

        // Verify service method was called
        verify(adminService).closeFine(eq(fineId));
    }

    @Test
    void testSetFinesRate_Success() throws Exception {
        float newRate = 500f;
        mockMvc.perform(put("/notifications/admin/fines/{newRate}/rate", newRate))
                .andExpect(status().isOk())
                .andExpect(content().string("Fine updated Correctly"));
        verify(adminService).setFinesRateDay(newRate);
    }


}