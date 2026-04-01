package com.esgi.lac.architecture.backend.application.repository;

import com.esgi.lac.architecture.backend.domain.model.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    boolean existsOverlappingSpotBooking(String spotId, LocalDate startDate, LocalDate endDate);
    boolean existsOverlappingUserBooking(String email, LocalDate startDate, LocalDate endDate);
    long countUpcomingDaysByUser(String email, LocalDate fromDate);
    void save(Booking booking);
    List<Booking> findAllOverlappingDate(LocalDate date);
    void deleteById(Long id);
    Optional<Booking> findById(Long id);
}
