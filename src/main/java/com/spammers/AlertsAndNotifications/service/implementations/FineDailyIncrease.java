package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.model.FineModel;
import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import com.spammers.AlertsAndNotifications.repository.FinesRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
/**
 * This class represents the Daily fine increase component to provide the
 * features to run a concurrent process to update the fines amount.
 * @since 12-12-2024
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class FineDailyIncrease {
    private final FinesRepository finesRepository;
    private final int limitDaysIncrement=20;
    private final int EXECUTIONS = 15;
    private int page = 0;
    @Getter
    private float fineRate = 800f; // 800 COP per day

    public void setFineRate(float fineRate) {
        if(invalidRate(fineRate)) throw new SpammersPrivateExceptions(SpammersPrivateExceptions.INVALID_RATE, 400);
        this.fineRate = fineRate;
    }

    private boolean invalidRate(float fineRate) {
        return fineRate < 0 || fineRate > 10000;
    }

    /**
     * This method checks every 5 minutes the pending fines
     * to increase the amount per day by a fine rate that
     * can be modified by admin.
     * This task is executed every day since 12:00 AM to 4:00 AM.
     */
    @Scheduled(cron="0 */5 0-4 * * MON-SUN")
    private void increaseFinesAmount(){
        processFines();
        page++;
        LocalTime comparisonTime = LocalTime.of(23,50);
        LocalTime now = LocalTime.now();
        if(now.isAfter(comparisonTime) || now.equals(comparisonTime)){
            page = 0;
        }

    }

    private void processFines() {
        List<FineModel> fines = fetchFines();
        for (FineModel fine : fines) {
            fine.setAmount(fine.getAmount() + fineRate);
            finesRepository.save(fine);
        }
    }

    private List<FineModel> fetchFines() {
        Pageable pageable = PageRequest.of(page, EXECUTIONS, Sort.by("expiredDate").descending());
        return finesRepository.pendingFinesAfter(LocalDate.now().minusDays(limitDaysIncrement),
                FineStatus.PENDING, pageable);
    }

}
