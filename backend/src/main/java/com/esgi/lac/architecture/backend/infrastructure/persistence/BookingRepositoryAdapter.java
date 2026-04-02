package com.esgi.lac.architecture.backend.infrastructure.persistence;

import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.application.repository.BookingRepository;
import com.esgi.lac.architecture.backend.infrastructure.persistence.entity.BookingEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
public class BookingRepositoryAdapter implements BookingRepository {

    private final JpaBookingRepository jpaBookingRepository;

    public BookingRepositoryAdapter(JpaBookingRepository jpaBookingRepository) {
        this.jpaBookingRepository = jpaBookingRepository;
    }

    @Override
    public boolean existsOverlappingSpotBooking(String spotId, LocalDate startDate, LocalDate endDate) {
        return jpaBookingRepository.existsOverlappingSpotBooking(spotId, startDate, endDate);
    }

    @Override
    public boolean existsOverlappingUserBooking(String email, LocalDate startDate, LocalDate endDate) {
        return jpaBookingRepository.existsOverlappingUserBooking(email, startDate, endDate);
    }

    @Override
    public long countUpcomingDaysByUser(String email, LocalDate fromDate) {
        List<BookingEntity> upcoming = jpaBookingRepository.findUpcomingByUser(email, fromDate);
        return upcoming.stream()
                .mapToLong(b -> {
                    // On ne compte que les jours >= fromDate
                    LocalDate effectiveStart = b.getStartDate().isBefore(fromDate) ? fromDate : b.getStartDate();
                    return ChronoUnit.DAYS.between(effectiveStart, b.getEndDate()) + 1;
                })
                .sum();
    }

    @Override
    public void save(Booking booking) {
        BookingEntity entity = new BookingEntity();
        entity.setSpotId(booking.spotId());
        entity.setEmail(booking.email());
        entity.setRole(booking.role().name());
        entity.setStartDate(booking.startDate());
        entity.setEndDate(booking.endDate());
        jpaBookingRepository.save(entity);
    }

    @Override
    public List<Booking> findAllOverlappingDate(LocalDate date) {
        return jpaBookingRepository.findAllOverlappingDate(date)
            .stream()
            .map(this::toDomain)
            .toList();
    }

    private UserRole parseRole(String role) {
        if (role == null || role.isBlank()) {
            return UserRole.EMPLOYEE;
        }
        return UserRole.valueOf(role);
    }

    @Override
    public void deleteById(Long id) {
        jpaBookingRepository.deleteById(id);
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return jpaBookingRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<Booking> findByEmailAndSpotIdForDate(String email, String spotId, LocalDate date) {
        return jpaBookingRepository.findByEmailAndSpotIdForDate(email, spotId, date)
                .map(this::toDomain);
    }

    @Override
    public void checkIn(Long bookingId) {
        jpaBookingRepository.checkIn(bookingId);
    }

    @Override
    public int resetCheckedInForMultiDayBookings(LocalDate today) {
        return jpaBookingRepository.resetCheckedInForMultiDayBookings(today);
    }

    @Override
    public List<Booking> findAllByUserEmail(String email) {
        return jpaBookingRepository.findAllByEmail(email).stream()
                .map(this::toDomain)
                .toList();
    }

    private Booking toDomain(BookingEntity entity) {
        return new Booking(
                entity.getId(),
                entity.getSpotId(),
                entity.getEmail(),
                parseRole(entity.getRole()),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.isCheckedIn()
        );
    }
}
