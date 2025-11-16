package com.flightbooking.search.scheduler;

import com.flightbooking.search.service.SeatHoldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatHoldExpiryScheduler {
    
    private final SeatHoldService seatHoldService;
    
    @Scheduled(fixedRate = 60000)
    public void releaseExpiredHolds() {
        log.debug("Running seat hold expiry job");
        try {
            seatHoldService.releaseExpiredHolds();
        } catch (Exception e) {
            log.error("Error in seat hold expiry job", e);
        }
    }
}
