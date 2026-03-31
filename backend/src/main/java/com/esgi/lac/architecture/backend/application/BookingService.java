package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.domain.usecase.BookingUseCase;
import com.esgi.lac.architecture.backend.infrastructure.persistence.JpaBookingRepository;
import com.esgi.lac.architecture.backend.infrastructure.persistence.entity.BookingEntity;
import com.esgi.lac.architecture.backend.domain.model.Booking;
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
    public void reserveSpot(Booking booking) { // <-- Vérifie que "request" est bien défini ici
        
        // 1. Validation de la durée via l'Enum
        if (booking.durationDays() > booking.role().getMaxDays()) {
            throw new IllegalArgumentException(
                "Le rôle " + booking.role() + " ne peut pas réserver plus de " + 
                booking.role().getMaxDays() + " jours."
            );
        }

        // 2. Création de l'entité pour la base de données
        BookingEntity entity = new BookingEntity();
        entity.setSpotId(booking.spotId());
        entity.setFirstName(booking.firstName());
        entity.setLastName(booking.lastName());
        entity.setDurationDays(booking.durationDays());
        // On pourrait aussi stocker le rôle si besoin : entity.setRole(request.role().name());

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