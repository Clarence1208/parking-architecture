package com.esgi.lac.architecture.backend.application.repository;

import com.esgi.lac.architecture.backend.domain.model.Booking;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository {
    boolean existsBySpotIdAndDate(String spotId, LocalDate bookingDate);
    boolean existsByEmailAndDate(String email, LocalDate bookingDate);
    long countUpcomingByUser(String email, LocalDate fromDate);
    void save(Booking booking);
    List<Booking> findAllByDate(LocalDate date);
}
