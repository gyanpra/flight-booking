package com.flightbooking.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PNRGeneratorTest {
    
    @Test
    void testGenerate_ReturnsValidPNR() {
        String pnr = PNRGenerator.generate();
        
        assertNotNull(pnr);
        assertEquals(6, pnr.length());
        assertTrue(pnr.matches("[A-Z0-9]{6}"));
    }
    
    @Test
    void testGenerate_ReturnsUniquePNRs() {
        String pnr1 = PNRGenerator.generate();
        String pnr2 = PNRGenerator.generate();
        
        assertNotEquals(pnr1, pnr2);
    }
}
