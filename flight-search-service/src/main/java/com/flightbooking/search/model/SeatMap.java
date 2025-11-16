package com.flightbooking.search.model;

import com.flightbooking.common.enums.CabinClass;
import com.flightbooking.common.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "seat_map", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"flight_id", "seat_no"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private UUID flightId;
    
    @Column(nullable = false)
    private String seatNo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CabinClass cabinClass;
    
    @Column(nullable = false)
    private String fareClass;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status = SeatStatus.AVAILABLE;
    
    private UUID currentHoldId;
}
