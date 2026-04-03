package com.esgi.lac.architecture.backend.infrastructure.persistence.mapper;

import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.infrastructure.persistence.entity.BookingEntity;
import org.springframework.stereotype.Component;

@Component
public class BookingEntityMapper {

    public Booking toDomain(BookingEntity entity) {
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

    public BookingEntity toEntity(Booking booking) {
        BookingEntity entity = new BookingEntity();
        entity.setSpotId(booking.spotId());
        entity.setEmail(booking.email());
        entity.setRole(booking.role().name());
        entity.setStartDate(booking.startDate());
        entity.setEndDate(booking.endDate());
        return entity;
    }

    private UserRole parseRole(String role) {
        if (role == null || role.isBlank()) {
            return UserRole.EMPLOYEE;
        }
        return UserRole.valueOf(role);
    }
}
