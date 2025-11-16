package com.flightbooking.search.repository;

import com.flightbooking.search.model.SeatInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeatInventoryRepository extends JpaRepository<SeatInventory, Long> {
    List<SeatInventory> findByFlightId(UUID flightId);
}
