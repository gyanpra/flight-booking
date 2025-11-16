package com.flightbooking.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.flightbooking.search", "com.flightbooking.common"})
public class FlightSearchApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FlightSearchApplication.class, args);
    }
}
