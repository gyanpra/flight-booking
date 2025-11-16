package com.flightbooking.search.controller;

import com.flightbooking.common.enums.CabinClass;
import com.flightbooking.search.dto.FlightDetailsResponse;
import com.flightbooking.search.dto.FlightSearchResponse;
import com.flightbooking.search.service.FlightSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightSearchController.class)
class FlightSearchControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private FlightSearchService flightSearchService;
    
    @Test
    void testSearchFlights() throws Exception {
        when(flightSearchService.searchFlights(any())).thenReturn(Arrays.asList(new FlightSearchResponse()));
        
        mockMvc.perform(get("/api/flights/search")
                .param("origin", "BLR")
                .param("destination", "DEL")
                .param("departureDate", "2025-12-12")
                .param("passengers", "2")
                .param("cabinClass", "ECONOMY"))
                .andExpect(status().isOk());
    }
    
    @Test
    void testGetFlightDetails() throws Exception {
        UUID flightId = UUID.randomUUID();
        when(flightSearchService.getFlightDetails(flightId)).thenReturn(new FlightDetailsResponse());
        
        mockMvc.perform(get("/api/flights/" + flightId))
                .andExpect(status().isOk());
    }
    
    @Test
    void testSyncToElasticsearch() throws Exception {
        mockMvc.perform(post("/api/flights/sync"))
                .andExpect(status().isOk());
    }
}
