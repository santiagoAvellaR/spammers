package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.model.FineModel;
import com.spammers.AlertsAndNotifications.model.dto.FineOutputDTO;
import com.spammers.AlertsAndNotifications.model.dto.PaginatedResponseDTO;
import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import com.spammers.AlertsAndNotifications.repository.FinesRepository;
import com.spammers.AlertsAndNotifications.service.interfaces.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private final FinesRepository finesRepository;

    /**
     * Retrieves all active fines (with status PENDING) and returns them in a paginated response.
     *
     * @param pageSize The number of records per page.
     * @param pageNumber The page number to retrieve.
     * @return A PaginatedResponseDTO containing the list of active fines and pagination details.
     */
    @Override
    public PaginatedResponseDTO<FineOutputDTO> returnAllActiveFines(int pageSize, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<FineModel> page = finesRepository.findByStatus(FineStatus.PENDING, pageable);
        return FineOutputDTO.encapsulateFineModelOnDTO(page);
    }

    /**
     * Retrieves all active fines (with status PENDING) within a specific date
     * and returns them in a paginated response.
     *
     * @param date The date to filter fines, according to the year and month of the date.
     * @param pageSize The number of records per page.
     * @param pageNumber The page number to retrieve.
     * @return A PaginatedResponseDTO containing the list of active fines and pagination details.
     */
    @Override
    public PaginatedResponseDTO<FineOutputDTO> returnAllActiveFinesBetweenDate(LocalDate date, int pageSize, int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<FineModel> page = finesRepository.findByStatusAndDate(FineStatus.PENDING, date, pageable);
        return FineOutputDTO.encapsulateFineModelOnDTO(page);
    }
}
