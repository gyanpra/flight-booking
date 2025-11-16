package com.flightbooking.search.service;

import java.util.List;
import java.util.UUID;

public interface SeatHoldService {
    UUID holdSeats(UUID flightId, String sessionId, List<String> seats, Integer durationMinutes);
    void releaseHold(UUID holdId);
    void confirmHold(UUID holdId);
    void releaseExpiredHolds();
}
