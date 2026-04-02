package com.esgi.lac.architecture.backend.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingConfirmationMessage {

    private Long bookingId;

    // Who to contact
    private String recipientEmail;

    // What spot was booked
    private String parkingSpotId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public BookingConfirmationMessage(Long bookingId, String recipientEmail, String parkingSpotId, LocalDate startDate, LocalDate endDate) {
    }
}