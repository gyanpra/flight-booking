package com.flightbooking.search.model;

import com.flightbooking.common.enums.CabinClass;
import com.flightbooking.common.enums.Carrier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(indexName = "itineraries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightDocument {
    @Id
    public String itineraryId;
    
    public UUID flightId;
    
    @Field(type = FieldType.Keyword)
    public Carrier carrier;
    
    public String flightNumber;
    
    @Field(type = FieldType.Keyword)
    public String origin;
    
    @Field(type = FieldType.Keyword)
    public String destination;
    
    @Field(type = FieldType.Date)
    public LocalDateTime departureTime;
    
    @Field(type = FieldType.Date)
    public LocalDateTime arrivalTime;
    
    public Integer durationMinutes;
    
    @Field(type = FieldType.Keyword)
    public CabinClass cabinClass;
    
    public BigDecimal price;
    
    public String currency;
    
    public Integer availableSeats;
    
    public Integer stops;
    
    public List<SegmentInfo> segments;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SegmentInfo {
        public UUID flightId;
        public Carrier carrier;
        public String flightNumber;
        public String origin;
        public String destination;
        public LocalDateTime departureTime;
        public LocalDateTime arrivalTime;
        public Integer durationMinutes;
        public CabinClass cabinClass;
    }
}
