package com.flightbooking.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.flightbooking.search", "com.flightbooking.common"})
@EnableScheduling
public class FlightSearchApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FlightSearchApplication.class, args);
    }
}
