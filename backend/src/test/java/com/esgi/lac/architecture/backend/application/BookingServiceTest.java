package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.infrastructure.persistence.JpaBookingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private JpaBookingRepository repository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    @DisplayName("Un employé ne doit pas pouvoir réserver plus de 5 jours")
    void employee_should_not_reserve_more_than_5_days() {
        // Given - On crée un objet Booking avec le rôle EMPLOYEE et 6 jours
        Booking booking = new Booking(
            "A01", "Alan", "Diot", 6, 
            UserRole.EMPLOYEE, LocalDateTime.now()
        );

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.reserveSpot(booking);
        });
    }

    @Test
    @DisplayName("Un manager peut réserver jusqu'à 30 jours")
    void manager_can_reserve_up_to_30_days() {
        // Given - Un manager qui demande 30 jours
        Booking booking = new Booking(
            "B02", "Chef", "Admin", 30, 
            UserRole.MANAGER, LocalDateTime.now()
        );
        
        // When
        bookingService.reserveSpot(booking);

        // Then - On vérifie que le repository a bien été appelé pour sauvegarder
        verify(repository, times(1)).save(any());
    }
}