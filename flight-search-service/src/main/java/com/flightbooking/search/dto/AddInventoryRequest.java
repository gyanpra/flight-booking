package com.flightbooking.search.dto;

import com.flightbooking.common.enums.CabinClass;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddInventoryRequest {
    @NotNull
    private UUID flightId;
    
    @NotBlank
    private String fareClass;
    
    @NotNull
    private CabinClass cabinClass;
    
    @NotNull
    @Min(1)
    private Integer totalSeats;
    
    @NotNull
    private BigDecimal price;
}
