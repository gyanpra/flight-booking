package com.flightbooking.booking.mapper;

import com.flightbooking.booking.dto.BookingResponse;
import com.flightbooking.booking.model.Booking;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BookingMapper {
    
    public BookingResponse toResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getBookingId());
        response.setPnr(booking.getPnr());
        response.setUserId(booking.getUserId());
        response.setItineraryId(booking.getItineraryId());
        response.setBookingStatus(booking.getBookingStatus());
        response.setAmount(booking.getAmount());
        response.setCurrency(booking.getCurrency());
        response.setCreatedAt(booking.getCreatedAt());
        response.setExpiresAt(booking.getExpiresAt());
        
        if (booking.getPassengers() != null) {
            response.setPassengers(booking.getPassengers().stream()
                .map(p -> new BookingResponse.PassengerInfo(
                    p.getFirstName(), p.getLastName(), p.getAge(), p.getGender()
                ))
                .collect(Collectors.toList()));
        }
        
        if (booking.getSeats() != null) {
            response.setSeats(booking.getSeats().stream()
                .map(s -> new BookingResponse.SeatInfo(
                    s.getFlightId(), s.getSeatNo(), s.getCabinClass()
                ))
                .collect(Collectors.toList()));
        }
        
        return response;
    }
}
