package com.flightbooking.search.service.impl;

import com.flightbooking.common.enums.HoldStatus;
import com.flightbooking.common.exception.BusinessException;
import com.flightbooking.search.model.InventoryHold;
import com.flightbooking.search.model.SeatInventory;
import com.flightbooking.search.repository.InventoryHoldRepository;
import com.flightbooking.search.repository.SeatInventoryRepository;
import com.flightbooking.search.service.SeatHoldService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatHoldServiceImpl implements SeatHoldService {
    
    private final InventoryHoldRepository holdRepository;
    private final SeatInventoryRepository inventoryRepository;
    private final RedissonClient redissonClient;
    
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(
        retryFor = {OptimisticLockException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public UUID holdSeats(UUID flightId, String sessionId, List<String> seats, Integer durationMinutes) {
        String lockKey = "seat-hold:" + flightId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                try {
                    log.info("Holding {} seats for flight: {}", seats.size(), flightId);
                    
                    List<SeatInventory> inventories = inventoryRepository.findByFlightId(flightId);
                    if (inventories.isEmpty()) {
                        throw new BusinessException("No inventory found for flight");
                    }
                    
                    SeatInventory inventory = inventories.get(0);
                    if (inventory.getAvailableSeats() < seats.size()) {
                        throw new BusinessException("Insufficient seats available");
                    }
                    
                    inventory.setAvailableSeats(inventory.getAvailableSeats() - seats.size());
                    inventoryRepository.save(inventory);
                    
                    InventoryHold hold = new InventoryHold();
                    hold.setFlightId(flightId);
                    hold.setCustomerSessionId(sessionId);
                    hold.setSeatCount(seats.size());
                    hold.setSeats(seats);
                    hold.setExpiresAt(LocalDateTime.now().plusMinutes(durationMinutes));
                    hold.setStatus(HoldStatus.ACTIVE);
                    hold = holdRepository.save(hold);
                    
                    log.info("Seats held successfully: {}", hold.getHoldId());
                    return hold.getHoldId();
                } finally {
                    lock.unlock();
                }
            } else {
                throw new BusinessException("Unable to acquire lock for seat hold");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("Seat hold interrupted");
        }
    }
    
    @Override
    @Transactional
    @Retryable(
        retryFor = {OptimisticLockException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public void releaseHold(UUID holdId) {
        InventoryHold hold = holdRepository.findById(holdId)
            .orElseThrow(() -> new BusinessException("Hold not found"));
        
        if (hold.getStatus() != HoldStatus.ACTIVE) {
            return;
        }
        
        String lockKey = "seat-hold:" + hold.getFlightId();
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                try {
                    List<SeatInventory> inventories = inventoryRepository.findByFlightId(hold.getFlightId());
                    if (!inventories.isEmpty()) {
                        SeatInventory inventory = inventories.get(0);
                        inventory.setAvailableSeats(inventory.getAvailableSeats() + hold.getSeatCount());
                        inventoryRepository.save(inventory);
                    }
                    
                    hold.setStatus(HoldStatus.RELEASED);
                    holdRepository.save(hold);
                    log.info("Hold released: {}", holdId);
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("Release hold interrupted");
        }
    }
    
    @Override
    @Transactional
    public void confirmHold(UUID holdId) {
        InventoryHold hold = holdRepository.findById(holdId)
            .orElseThrow(() -> new BusinessException("Hold not found"));
        
        hold.setStatus(HoldStatus.CONFIRMED);
        holdRepository.save(hold);
        log.info("Hold confirmed: {}", holdId);
    }
    
    @Override
    @Transactional
    public void releaseExpiredHolds() {
        List<InventoryHold> expiredHolds = holdRepository.findExpiredHolds(LocalDateTime.now());
        
        for (InventoryHold hold : expiredHolds) {
            try {
                releaseHold(hold.getHoldId());
            } catch (Exception e) {
                log.error("Error releasing expired hold: {}", hold.getHoldId(), e);
            }
        }
        
        log.info("Released {} expired holds", expiredHolds.size());
    }
}
