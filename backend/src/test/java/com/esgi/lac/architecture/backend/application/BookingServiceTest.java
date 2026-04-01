package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.application.repository.BookingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository repository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    @DisplayName("Un employé ne doit pas pouvoir réserver plus de 5 jours")
    void employee_should_not_reserve_more_than_5_days() {
        when(repository.existsByEmailAndDate("alan@test.com", LocalDate.now().plusDays(1))).thenReturn(false);
        when(repository.existsBySpotIdAndDate("A01", LocalDate.now().plusDays(1))).thenReturn(false);
        when(repository.countUpcomingByUser("alan@test.com", LocalDate.now())).thenReturn(5L);

        Booking booking = new Booking(
            "A01", "alan@test.com",
            UserRole.EMPLOYEE, LocalDate.now().plusDays(1)
        );

        assertThrows(IllegalArgumentException.class, () -> bookingService.reserveSpot(booking));
    }

    @Test
    @DisplayName("Un manager peut réserver jusqu'à 30 jours")
    void manager_can_reserve_up_to_30_days() {
        when(repository.existsByEmailAndDate("chef@test.com", LocalDate.now().plusDays(1))).thenReturn(false);
        when(repository.existsBySpotIdAndDate("B02", LocalDate.now().plusDays(1))).thenReturn(false);
        when(repository.countUpcomingByUser("chef@test.com", LocalDate.now())).thenReturn(29L);

        Booking booking = new Booking(
            "B02", "chef@test.com",
            UserRole.MANAGER, LocalDate.now().plusDays(1)
        );

        bookingService.reserveSpot(booking);

        verify(repository, times(1)).save(any());
    }
}