package com.flightbooking.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.flightbooking.notification", "com.flightbooking.common"})
public class FlightNotificationApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FlightNotificationApplication.class, args);
    }
}
