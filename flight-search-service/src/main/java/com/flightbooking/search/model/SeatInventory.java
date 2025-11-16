package com.flightbooking.search.model;

import com.flightbooking.common.enums.CabinClass;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "seat_inventory", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"flight_id", "fare_class"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private UUID flightId;
    
    @Column(nullable = false)
    private String fareClass;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CabinClass cabinClass;
    
    @Column(nullable = false)
    private Integer totalSeats;
    
    @Column(nullable = false)
    private Integer availableSeats;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Version
    private Long version;
}
