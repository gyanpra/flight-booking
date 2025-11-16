package com.flightbooking.common.util;

import java.security.SecureRandom;

public class PNRGenerator {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int PNR_LENGTH = 6;
    
    public static String generate() {
        StringBuilder pnr = new StringBuilder(PNR_LENGTH);
        for (int i = 0; i < PNR_LENGTH; i++) {
            pnr.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return pnr.toString();
    }
}
