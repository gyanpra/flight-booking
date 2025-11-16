package com.flightbooking.booking.dto;

import com.flightbooking.common.dto.PassengerDTO;
import com.flightbooking.common.dto.SeatSelectionDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    @NotNull
    private UUID userId;
    
    @NotNull
    private UUID itineraryId;
    
    @NotEmpty
    @Valid
    private List<PassengerDTO> passengers;
    
    @NotEmpty
    @Valid
    private List<SeatSelectionDTO> seats;
}
