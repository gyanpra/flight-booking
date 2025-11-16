package com.flightbooking.search.repository;

import com.flightbooking.common.enums.HoldStatus;
import com.flightbooking.search.model.InventoryHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryHoldRepository extends JpaRepository<InventoryHold, UUID> {
    
    @Query("SELECT h FROM InventoryHold h WHERE h.status = 'ACTIVE' AND h.expiresAt < :now")
    List<InventoryHold> findExpiredHolds(LocalDateTime now);
    
    List<InventoryHold> findByCustomerSessionIdAndStatus(String sessionId, HoldStatus status);
}
