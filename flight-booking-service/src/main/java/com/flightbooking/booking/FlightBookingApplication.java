package com.flightbooking.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.flightbooking.booking", "com.flightbooking.common"})
public class FlightBookingApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FlightBookingApplication.class, args);
    }
}
