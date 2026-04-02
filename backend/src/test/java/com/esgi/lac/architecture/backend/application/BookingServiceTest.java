package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.application.service.BookingService;
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
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);

        when(repository.existsOverlappingUserBooking("alan@test.com", start, end)).thenReturn(false);
        when(repository.existsOverlappingSpotBooking("A01", start, end)).thenReturn(false);
        when(repository.countUpcomingDaysByUser("alan@test.com", LocalDate.now())).thenReturn(5L);

        Booking booking = new Booking(
            "A01", "alan@test.com",
            UserRole.EMPLOYEE, start, end
        );

        assertThrows(IllegalArgumentException.class, () -> bookingService.reserveSpot(booking));
    }

    @Test
    @DisplayName("Un manager peut réserver jusqu'à 30 jours")
    void manager_can_reserve_up_to_30_days() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);

        when(repository.existsOverlappingUserBooking("chef@test.com", start, end)).thenReturn(false);
        when(repository.existsOverlappingSpotBooking("B02", start, end)).thenReturn(false);
        when(repository.countUpcomingDaysByUser("chef@test.com", LocalDate.now())).thenReturn(29L);

        Booking booking = new Booking(
            "B02", "chef@test.com",
            UserRole.MANAGER, start, end
        );

        bookingService.reserveSpot(booking);

        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Un employé peut réserver un intervalle de plusieurs jours")
    void employee_can_reserve_multi_day_range() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3); // 3 jours

        when(repository.existsOverlappingUserBooking("alan@test.com", start, end)).thenReturn(false);
        when(repository.existsOverlappingSpotBooking("A01", start, end)).thenReturn(false);
        when(repository.countUpcomingDaysByUser("alan@test.com", LocalDate.now())).thenReturn(0L);

        Booking booking = new Booking(
            "A01", "alan@test.com",
            UserRole.EMPLOYEE, start, end
        );

        bookingService.reserveSpot(booking);

        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Un employé ne peut pas dépasser son quota avec un intervalle multi-jours")
    void employee_cannot_exceed_quota_with_multi_day() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3); // 3 jours demandés

        when(repository.existsOverlappingUserBooking("alan@test.com", start, end)).thenReturn(false);
        when(repository.existsOverlappingSpotBooking("A01", start, end)).thenReturn(false);
        when(repository.countUpcomingDaysByUser("alan@test.com", LocalDate.now())).thenReturn(3L); // 3 + 3 = 6 > 5

        Booking booking = new Booking(
            "A01", "alan@test.com",
            UserRole.EMPLOYEE, start, end
        );

        assertThrows(IllegalArgumentException.class, () -> bookingService.reserveSpot(booking));
    }
}