package com.esgi.lac.architecture.backend.infrastructure.persistence;

import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.application.repository.BookingRepository;
import com.esgi.lac.architecture.backend.infrastructure.persistence.entity.BookingEntity;
import com.esgi.lac.architecture.backend.infrastructure.persistence.mapper.BookingEntityMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
public class BookingRepositoryAdapter implements BookingRepository {

    private final JpaBookingRepository jpaBookingRepository;
    private final BookingEntityMapper bookingEntityMapper;

    public BookingRepositoryAdapter(JpaBookingRepository jpaBookingRepository, BookingEntityMapper bookingEntityMapper) {
        this.jpaBookingRepository = jpaBookingRepository;
        this.bookingEntityMapper = bookingEntityMapper;
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
        jpaBookingRepository.save(bookingEntityMapper.toEntity(booking));
    }

    @Override
    public List<Booking> findAllOverlappingDate(LocalDate date) {
        return jpaBookingRepository.findAllOverlappingDate(date)
            .stream()
            .map(bookingEntityMapper::toDomain)
            .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaBookingRepository.deleteById(id);
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return jpaBookingRepository.findById(id)
                .map(bookingEntityMapper::toDomain);
    }

    @Override
    public Optional<Booking> findByEmailAndSpotIdForDate(String email, String spotId, LocalDate date) {
        return jpaBookingRepository.findByEmailAndSpotIdForDate(email, spotId, date)
                .map(bookingEntityMapper::toDomain);
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
    public List<Booking> findUncheckedForDate(LocalDate date) {
        return jpaBookingRepository.findUncheckedForDate(date).stream()
                .map(bookingEntityMapper::toDomain)
                .toList();
    }

    @Override
    public void updateStartDate(Long id, LocalDate newStartDate) {
        jpaBookingRepository.updateStartDate(id, newStartDate);
    }

    @Override
    public void updateEndDate(Long id, LocalDate newEndDate) {
        jpaBookingRepository.updateEndDate(id, newEndDate);
    }

    @Override
    public List<Booking> findAllByUserEmail(String email) {
        return jpaBookingRepository.findAllByEmail(email).stream()
                .map(bookingEntityMapper::toDomain)
                .toList();
    }
}
