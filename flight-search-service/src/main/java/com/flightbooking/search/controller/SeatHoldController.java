package com.flightbooking.search.controller;

import com.flightbooking.search.service.SeatHoldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/seat-holds")
@RequiredArgsConstructor
public class SeatHoldController {
    
    private final SeatHoldService seatHoldService;
    
    @PostMapping
    public ResponseEntity<Map<String, UUID>> holdSeats(
            @RequestParam UUID flightId,
            @RequestParam String sessionId,
            @RequestBody List<String> seats,
            @RequestParam(defaultValue = "15") Integer durationMinutes) {
        UUID holdId = seatHoldService.holdSeats(flightId, sessionId, seats, durationMinutes);
        return ResponseEntity.ok(Map.of("holdId", holdId));
    }
    
    @DeleteMapping("/{holdId}")
    public ResponseEntity<Void> releaseHold(@PathVariable UUID holdId) {
        seatHoldService.releaseHold(holdId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{holdId}/confirm")
    public ResponseEntity<Void> confirmHold(@PathVariable UUID holdId) {
        seatHoldService.confirmHold(holdId);
        return ResponseEntity.ok().build();
    }
}
