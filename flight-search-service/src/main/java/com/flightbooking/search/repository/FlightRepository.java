package com.flightbooking.search.repository;

import com.flightbooking.search.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface FlightRepository extends JpaRepository<Flight, UUID> {
    
    @Query("SELECT f FROM Flight f WHERE f.departureAirport = :origin " +
           "AND f.arrivalAirport = :destination " +
           "AND f.departureTime >= :startTime " +
           "AND f.departureTime < :endTime " +
           "AND f.isActive = true")
    List<Flight> findFlightsByRoute(String origin, String destination, 
                                     LocalDateTime startTime, LocalDateTime endTime);
}
