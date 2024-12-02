package com.spammers.AlertsAndNotifications.controller;

import com.spammers.AlertsAndNotifications.model.dto.FineOutputDTO;
import com.spammers.AlertsAndNotifications.model.dto.PaginatedResponseDTO;
import com.spammers.AlertsAndNotifications.service.interfaces.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.time.LocalDate;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.when;

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
}