package com.esgi.lac.architecture.backend.application.service;

import com.esgi.lac.architecture.backend.application.dto.BookingConfirmationMessage;
import com.esgi.lac.architecture.backend.application.repository.BookingQueuePort;
import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.BookingSpotStatus;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.application.repository.BookingRepository;
import com.esgi.lac.architecture.backend.application.usecase.BookingUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService implements BookingUseCase {

    private final BookingRepository repository;
    private final BookingQueuePort bookingPublisher;

    public BookingService(BookingRepository repository, BookingQueuePort bookingPublisher) {
        this.repository = repository;
        this.bookingPublisher = bookingPublisher;
    }

    @Override
    @Transactional
    public void reserveSpot(Booking booking) {
        // Validation des dates
        if (booking.startDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("On ne peut pas réserver dans le passé !");
        }
        if (booking.endDate().isBefore(booking.startDate())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être avant la date de début !");
        }

        // Vérifier si l'utilisateur a déjà une réservation qui chevauche cet intervalle
        if (repository.existsOverlappingUserBooking(booking.email(), booking.startDate(), booking.endDate())) {
            throw new IllegalStateException("Vous avez déjà une réservation qui chevauche ces dates !");
        }

        // Vérifier si la place est déjà prise sur cet intervalle
        if (repository.existsOverlappingSpotBooking(booking.spotId(), booking.startDate(), booking.endDate())) {
            throw new IllegalStateException("Cette place est déjà réservée sur cet intervalle.");
        }

        // Calculer le nombre de jours demandés
        long requestedDays = ChronoUnit.DAYS.between(booking.startDate(), booking.endDate()) + 1;

        // Vérifier le quota (5 ou 30 jours selon le rôle)
        long currentBookedDays = repository.countUpcomingDaysByUser(
            booking.email(),
            LocalDate.now()
        );
        long maxDays = booking.role().getMaxNumberOfBookingDays();
        if (currentBookedDays + requestedDays > maxDays) {
            throw new IllegalArgumentException(
                "Quota de " + maxDays + " jours atteint ! Vous avez déjà " + currentBookedDays +
                " jour(s) réservé(s) et vous demandez " + requestedDays + " jour(s)."
            );
        }

        // Sauvegarde
        repository.save(booking);

        // Envoi d'un message dans la queue
        BookingConfirmationMessage message = new BookingConfirmationMessage(
                booking.id(),
                booking.email(),
                booking.spotId(),
                booking.startDate(),
                booking.endDate()
        );

        bookingPublisher.publish(message);

    }

    @Override
    public List<BookingSpotStatus> getSpotsByDate(LocalDate date) {
        // On récupère les réservations dont l'intervalle couvre la date demandée
        List<Booking> dayBookings = repository.findAllOverlappingDate(date);

        return dayBookings.stream()
            .map(b -> new BookingSpotStatus(
                    b.id(),
                    b.spotId(),
                    true,
                    b.email(),
                    date
            ))
            .toList();
    }

    @Override
    public List<BookingSpotStatus> getAllSpots() {
        return getSpotsByDate(LocalDate.now());
    }

    @Override
    public long getRemainingDays(String email, UserRole role) {
        long usedDays = repository.countUpcomingDaysByUser(email, LocalDate.now());
        long maxDays = role.getMaxNumberOfBookingDays();
        return Math.max(0, maxDays - usedDays);
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId, String currentUserEmail, UserRole currentUserRole) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable"));
        boolean isOwner = booking.email().equals(currentUserEmail);
        boolean isSecretary = currentUserRole == UserRole.SECRETARY;

        if (!isOwner && !isSecretary) {
            throw new IllegalStateException("Vous n'avez pas les droits pour annuler cette réservation.");
        }
        repository.deleteById(bookingId);
    }

    @Override
    public List<Booking> getUserBookings(String email) {
        return repository.findAllByUserEmail(email);
    }

}