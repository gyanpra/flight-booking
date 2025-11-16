package com.flightbooking.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatSelectionDTO {
    @NotNull
    private UUID flightId;
    
    @NotBlank
    private String seatNo;
}
