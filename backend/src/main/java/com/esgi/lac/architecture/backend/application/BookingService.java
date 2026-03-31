package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.BookingSpotStatus;
import com.esgi.lac.architecture.backend.application.repository.BookingRepository;
import com.esgi.lac.architecture.backend.application.usecase.BookingUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService implements BookingUseCase {

    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
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
        if (repository.existsBySpotIdAndDate(booking.spotId(), booking.bookingDate())) {
            throw new IllegalStateException("Cette place est déjà réservée pour cette date.");
        }

        // Vérifier le quota (5 ou 30 jours selon le rôle)
        long currentBookings = repository.countUpcomingByUser(
            booking.firstName(),
            booking.lastName(),
            LocalDate.now()
        );
        if (currentBookings >= booking.role().getMaxNumberOfBookingDays()) {
            throw new IllegalArgumentException("Quota de " + booking.role().getMaxNumberOfBookingDays() + " jours atteint !");
        }

        // Sauvegarde
        repository.save(booking);
    }

    @Override
    public List<BookingSpotStatus> getSpotsByDate(LocalDate date) {
        // On récupère uniquement les réservations du jour 'date'
        List<Booking> dayBookings = repository.findAllByDate(date);

        return dayBookings.stream()
            .map(b -> new BookingSpotStatus(
                b.spotId(),
                true,
                b.firstName() + " " + b.lastName(),
                b.bookingDate()
            ))
            .toList();
    }

    @Override
    public List<BookingSpotStatus> getAllSpots() {
        return getSpotsByDate(LocalDate.now());
    }
}