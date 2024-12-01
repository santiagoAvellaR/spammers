package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.model.FineModel;
import com.spammers.AlertsAndNotifications.model.FineOutputDTO;
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

    private Map<String, Object> putDataOnMap(Page<FineModel> page){
        List<FineOutputDTO> fineInputDTOSList = page.getContent().stream().map(this::fineModelToOutputDTO).toList();
        Map<String, Object> map = new HashMap<>();
        map.put("data", fineInputDTOSList);
        map.put("currentPage", page.getNumber());
        map.put("totalPages", page.getTotalPages());
        map.put("totalItems", page.getTotalElements());
        return map;
    }

    @Override
    public Map<String, Object> returnAllActiveFines(int pageSize, int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<FineModel> page =  finesRepository.findByStatus(FineStatus.PENDING,  pageable);
        return putDataOnMap(page);
    }

    @Override
    public Map<String, Object> returnAllActiveFinesBetweenDate(LocalDate date, int pageSize, int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<FineModel> page = finesRepository.findByStatusAndDate(FineStatus.PENDING, date, pageable);
        return putDataOnMap(page);
    }
}
