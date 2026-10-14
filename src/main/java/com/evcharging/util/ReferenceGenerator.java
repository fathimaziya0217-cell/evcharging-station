package com.evcharging.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ReferenceGenerator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String generateBookingReference() {
        String datePart = LocalDate.now().format(DATE_FMT);
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "EVC-" + datePart + "-" + randomPart;
    }

    public static String generatePaymentReference() {
        String datePart = LocalDate.now().format(DATE_FMT);
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "PAY-" + datePart + "-" + randomPart;
    }

    public static String generateSessionReference() {
        String datePart = LocalDate.now().format(DATE_FMT);
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "SES-" + datePart + "-" + randomPart;
    }
}
