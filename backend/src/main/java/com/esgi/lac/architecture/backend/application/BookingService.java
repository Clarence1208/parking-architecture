package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.domain.usecase.BookingUseCase;
import com.esgi.lac.architecture.backend.infrastructure.persistence.JpaBookingRepository;
import com.esgi.lac.architecture.backend.infrastructure.persistence.entity.BookingEntity;
import com.esgi.lac.architecture.backend.domain.model.Booking;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    public void reserveSpot(Booking booking) {
        //Validation de la date
        if (booking.bookingDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("On ne peut pas réserver dans le passé !");
        }

        // Vérifier si la place est déjà prise CE JOUR-LÀ
        if (repository.findBySpotIdAndBookingDate(booking.spotId(), booking.bookingDate()).isPresent()) {
            throw new IllegalStateException("Cette place est déjà réservée pour cette date.");
        }

        // Vérifier le quota (5 ou 30 jours selon le rôle)
        long currentBookings = repository.countByFirstNameAndLastName(booking.firstName(), booking.lastName());
        if (currentBookings >= booking.role().getMaxDays()) {
            throw new IllegalArgumentException("Quota de " + booking.role().getMaxDays() + " jours atteint !");
        }

        // Sauvegarde
        BookingEntity entity = new BookingEntity();
        entity.setSpotId(booking.spotId());
        entity.setFirstName(booking.firstName());
        entity.setLastName(booking.lastName());
        entity.setBookingDate(booking.bookingDate());

        repository.save(entity);
    }

    @Override
    public List<Map<String, Object>> getSpotsByDate(LocalDate date) {
        // On récupère uniquement les réservations du jour 'date'
        List<BookingEntity> dayBookings = repository.findAllByBookingDate(date);

        // On transforme les entités en Map pour le Front
        return dayBookings.stream()
                .map(b -> {
                    Map<String, Object> spot = new HashMap<>();
                    spot.put("id", b.getSpotId());
                    spot.put("isOccupied", true);
                    spot.put("reservedBy", b.getFirstName() + " " + b.getLastName());
                    spot.put("date", b.getBookingDate());
                    return spot;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getAllSpots() {
        return getSpotsByDate(LocalDate.now());
    }
}