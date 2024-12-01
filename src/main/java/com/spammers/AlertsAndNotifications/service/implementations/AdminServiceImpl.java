package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.model.FineModel;
import com.spammers.AlertsAndNotifications.model.FineOutputDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private final FinesRepository finesRepository;

    private FineOutputDTO fineModelToOutputDTO(FineModel fineModel){
        return new FineOutputDTO(fineModel.getFineId(), fineModel.getDescription(), fineModel.getAmount(),
                fineModel.getFineStatus(), fineModel.getFineType(), fineModel.getExpiredDate(), fineModel.getLoan().getBookName());
    }

    private PaginatedResponseDTO<FineOutputDTO> encapsulateFineModelOnDTO(Page<FineModel> page){
        List<FineOutputDTO> fineOutputDTOList = page.getContent().stream()
                .map(this::fineModelToOutputDTO)
                .toList();

        return new PaginatedResponseDTO<>(
                fineOutputDTOList,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    @Override
    public PaginatedResponseDTO<FineOutputDTO> returnAllActiveFines(int pageSize, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<FineModel> page = finesRepository.findByStatus(FineStatus.PENDING, pageable);
        return encapsulateFineModelOnDTO(page);
    }


    @Override
    public PaginatedResponseDTO<FineOutputDTO> returnAllActiveFinesBetweenDate(LocalDate date, int pageSize, int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<FineModel> page = finesRepository.findByStatusAndDate(FineStatus.PENDING, date, pageable);
        return encapsulateFineModelOnDTO(page);
    }
}
