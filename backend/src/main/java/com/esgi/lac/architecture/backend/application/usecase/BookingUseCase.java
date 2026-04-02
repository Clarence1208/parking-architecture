package com.esgi.lac.architecture.backend.application.usecase;
import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.BookingSpotStatus;
import com.esgi.lac.architecture.backend.domain.model.UserRole;

import java.time.LocalDate;
import java.util.List;

public interface BookingUseCase {
    void reserveSpot(Booking booking);

    List<BookingSpotStatus> getSpotsByDate(LocalDate date);
    List<BookingSpotStatus> getAllSpots();
    long getRemainingDays(String email, UserRole role);
    void cancelBooking(Long bookingId, String currentUserEmail, UserRole currentUserRole);
    List<Booking> getUserBookings(String email);
    Booking checkIn(String spotId, String email);
}