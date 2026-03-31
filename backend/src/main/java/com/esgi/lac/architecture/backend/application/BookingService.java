package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.domain.usecase.BookingUseCase;
import com.esgi.lac.architecture.backend.infrastructure.persistence.JpaBookingRepository;
import com.esgi.lac.architecture.backend.infrastructure.persistence.entity.BookingEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

@Service
public class BookingService implements BookingUseCase {

    private final JpaBookingRepository repository;

    public BookingService(JpaBookingRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void reserveSpot(String spotId, String firstName, String lastName, int durationDays) {
        if (repository.existsById(spotId)) {
            throw new IllegalStateException("Place déjà réservée !");
        }

        BookingEntity entity = new BookingEntity(
            spotId, firstName, lastName, durationDays, LocalDateTime.now()
        );
        
        repository.save(entity);
    }

@Override
public List<Map<String, Object>> getAllSpots() {
    return repository.findAll().stream()
        .map(b -> {
            Map<String, Object> spot = new HashMap<>();
            spot.put("id", b.getSpotId());
            spot.put("isOccupied", true);
            spot.put("reservedBy", b.getFirstName() + " " + b.getLastName());
            return spot;
        })
        .collect(Collectors.toList());
}
}