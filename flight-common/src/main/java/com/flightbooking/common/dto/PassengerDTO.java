package com.flightbooking.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDTO {
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    @NotNull
    @Min(0)
    private Integer age;
    
    @NotBlank
    private String gender;
    
    private String documentType;
    private String documentNumber;
}
