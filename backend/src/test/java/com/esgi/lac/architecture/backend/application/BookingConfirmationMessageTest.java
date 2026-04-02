package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.application.dto.BookingConfirmationMessage;
import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class BookingConfirmationMessageTest {

    @Test
    @DisplayName("fromBooking maps all fields correctly")
    void fromBookingMapsAllFields() {
        Booking booking = new Booking(
                42L, "A01", "user@test.com", UserRole.EMPLOYEE,
                LocalDate.of(2026, 4, 10), LocalDate.of(2026, 4, 12), false
        );

        BookingConfirmationMessage message = BookingConfirmationMessage.fromBooking(booking);

        assertThat(message.getBookingId()).isEqualTo(42L);
        assertThat(message.getRecipientEmail()).isEqualTo("user@test.com");
        assertThat(message.getParkingSpotId()).isEqualTo("A01");
        assertThat(message.getStartDate()).isEqualTo(LocalDate.of(2026, 4, 10));
        assertThat(message.getEndDate()).isEqualTo(LocalDate.of(2026, 4, 12));
    }

    @Test
    @DisplayName("fromBooking handles null id")
    void fromBookingHandlesNullId() {
        Booking booking = new Booking(
                null, "B02", "user@test.com", UserRole.MANAGER,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1), false
        );

        BookingConfirmationMessage message = BookingConfirmationMessage.fromBooking(booking);

        assertThat(message.getBookingId()).isNull();
        assertThat(message.getParkingSpotId()).isEqualTo("B02");
    }
}
