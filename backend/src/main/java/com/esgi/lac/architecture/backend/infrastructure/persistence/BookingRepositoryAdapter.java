package com.esgi.lac.architecture.backend.infrastructure.persistence;

import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.application.repository.BookingRepository;
import com.esgi.lac.architecture.backend.infrastructure.persistence.entity.BookingEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class BookingRepositoryAdapter implements BookingRepository {

    private final JpaBookingRepository jpaBookingRepository;

    public BookingRepositoryAdapter(JpaBookingRepository jpaBookingRepository) {
        this.jpaBookingRepository = jpaBookingRepository;
    }

    @Override
    public boolean existsBySpotIdAndDate(String spotId, LocalDate bookingDate) {
        return jpaBookingRepository.findBySpotIdAndBookingDate(spotId, bookingDate).isPresent();
    }

    @Override
    public long countUpcomingByUser(String firstName, String lastName, LocalDate fromDate) {
        return jpaBookingRepository.countByFirstNameAndLastNameAndBookingDateGreaterThanEqual(firstName, lastName, fromDate);
    }

    @Override
    public void save(Booking booking) {
        BookingEntity entity = new BookingEntity();
        entity.setSpotId(booking.spotId());
        entity.setFirstName(booking.firstName());
        entity.setLastName(booking.lastName());
        entity.setRole(booking.role().name());
        entity.setBookingDate(booking.bookingDate());
        jpaBookingRepository.save(entity);
    }

    @Override
    public List<Booking> findAllByDate(LocalDate date) {
        return jpaBookingRepository.findAllByBookingDate(date)
            .stream()
            .map(entity -> new Booking(
                entity.getSpotId(),
                entity.getFirstName(),
                entity.getLastName(),
                parseRole(entity.getRole()),
                entity.getBookingDate()
            ))
            .toList();
    }

    private UserRole parseRole(String role) {
        if (role == null || role.isBlank()) {
            return UserRole.EMPLOYEE;
        }
        return UserRole.valueOf(role);
    }
}
