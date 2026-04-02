package com.esgi.lac.architecture.backend.application.dto;

import com.esgi.lac.architecture.backend.domain.model.Booking;
import java.time.LocalDate;

public class BookingConfirmationMessage {

    private Long bookingId;

    // Who to contact
    private String recipientEmail;

    // What spot was booked
    private String parkingSpotId;
    private LocalDate startDate;
    private LocalDate endDate;

    private BookingConfirmationMessage(
            Long bookingId,
            String recipientEmail,
            String parkingSpotId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        this.bookingId = bookingId;
        this.recipientEmail = recipientEmail;
        this.parkingSpotId = parkingSpotId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static BookingConfirmationMessage fromBooking(Booking booking) {
        return new BookingConfirmationMessage(
                booking.id(),
                booking.email(),
                booking.spotId(),
                booking.startDate(),
                booking.endDate()
        );
    }
}