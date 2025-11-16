package com.flightbooking.search.mapper;

import com.flightbooking.search.dto.FlightDetailsResponse;
import com.flightbooking.search.dto.FlightSearchResponse;
import com.flightbooking.search.model.Flight;
import com.flightbooking.search.model.FlightDocument;
import com.flightbooking.search.model.SeatInventory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlightMapper {
    
    public FlightSearchResponse toSearchResponse(FlightDocument doc) {
        FlightSearchResponse response = new FlightSearchResponse();
        response.setFlightId(doc.getFlightId());
        response.setCarrier(doc.getCarrier());
        response.setFlightNumber(doc.getFlightNumber());
        response.setOrigin(doc.getOrigin());
        response.setDestination(doc.getDestination());
        response.setDepartureTime(doc.getDepartureTime());
        response.setArrivalTime(doc.getArrivalTime());
        response.setDurationMinutes(doc.getDurationMinutes());
        response.setCabinClass(doc.getCabinClass());
        response.setPrice(doc.getPrice());
        response.setCurrency(doc.getCurrency());
        response.setAvailableSeats(doc.getAvailableSeats());
        return response;
    }
    
    public FlightDetailsResponse toDetailsResponse(Flight flight, List<SeatInventory> inventory) {
        FlightDetailsResponse response = new FlightDetailsResponse();
        response.setFlightId(flight.getFlightId());
        response.setCarrier(flight.getCarrier());
        response.setFlightNumber(flight.getFlightNumber());
        response.setDepartureAirport(flight.getDepartureAirport());
        response.setArrivalAirport(flight.getArrivalAirport());
        response.setDepartureTime(flight.getDepartureTime());
        response.setArrivalTime(flight.getArrivalTime());
        response.setEquipment(flight.getEquipment());
        response.setIsActive(flight.getIsActive());
        
        List<FlightDetailsResponse.SeatInventoryInfo> seatInfo = inventory.stream()
            .map(inv -> new FlightDetailsResponse.SeatInventoryInfo(
                inv.getFareClass(),
                inv.getCabinClass().name(),
                inv.getAvailableSeats(),
                inv.getPrice()
            ))
            .collect(Collectors.toList());
        response.setSeatInventory(seatInfo);
        
        return response;
    }
    
    public FlightDocument toDocument(Flight flight, SeatInventory inventory) {
        FlightDocument doc = new FlightDocument();
        doc.setItineraryId(flight.getFlightId().toString());
        doc.setFlightId(flight.getFlightId());
        doc.setCarrier(flight.getCarrier());
        doc.setFlightNumber(flight.getFlightNumber());
        doc.setOrigin(flight.getDepartureAirport());
        doc.setDestination(flight.getArrivalAirport());
        doc.setDepartureTime(flight.getDepartureTime());
        doc.setArrivalTime(flight.getArrivalTime());
        doc.setDurationMinutes((int) Duration.between(flight.getDepartureTime(), 
                                                       flight.getArrivalTime()).toMinutes());
        doc.setCabinClass(inventory.getCabinClass());
        doc.setPrice(inventory.getPrice());
        doc.setCurrency("INR");
        doc.setAvailableSeats(inventory.getAvailableSeats());
        doc.setStops(0);
        return doc;
    }
}
