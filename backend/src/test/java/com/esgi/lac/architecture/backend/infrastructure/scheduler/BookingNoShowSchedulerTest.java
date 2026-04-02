package com.esgi.lac.architecture.backend.infrastructure.scheduler;

import com.esgi.lac.architecture.backend.application.repository.BookingRepository;
import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingNoShowSchedulerTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingNoShowScheduler scheduler;

    @Test
    @DisplayName("single-day booking is deleted when unchecked")
    void singleDayBookingDeleted() {
        LocalDate today = LocalDate.now();
        Booking booking = new Booking(1L, "A01", "user@test.com", UserRole.EMPLOYEE,
                today, today, false);
        when(bookingRepository.findUncheckedForDate(today)).thenReturn(List.of(booking));

        scheduler.liberateUncheckedSpots();

        verify(bookingRepository).deleteById(1L);
        verify(bookingRepository, never()).updateStartDate(anyLong(), any());
        verify(bookingRepository, never()).updateEndDate(anyLong(), any());
    }

    @Test
    @DisplayName("multi-day booking starting today has start date shifted forward")
    void multiDayStartingTodayShiftsStart() {
        LocalDate today = LocalDate.now();
        Booking booking = new Booking(1L, "A01", "user@test.com", UserRole.EMPLOYEE,
                today, today.plusDays(3), false);
        when(bookingRepository.findUncheckedForDate(today)).thenReturn(List.of(booking));

        scheduler.liberateUncheckedSpots();

        verify(bookingRepository).updateStartDate(1L, today.plusDays(1));
        verify(bookingRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("multi-day booking ending today has end date shifted backward")
    void multiDayEndingTodayShiftsEnd() {
        LocalDate today = LocalDate.now();
        Booking booking = new Booking(1L, "A01", "user@test.com", UserRole.EMPLOYEE,
                today.minusDays(3), today, false);
        when(bookingRepository.findUncheckedForDate(today)).thenReturn(List.of(booking));

        scheduler.liberateUncheckedSpots();

        verify(bookingRepository).updateEndDate(1L, today.minusDays(1));
        verify(bookingRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("multi-day booking with today in the middle is split around today")
    void middleOfMultiDayBookingIsSplit() {
        LocalDate today = LocalDate.now();
        Booking booking = new Booking(1L, "A01", "user@test.com", UserRole.EMPLOYEE,
                today.minusDays(2), today.plusDays(2), false);
        when(bookingRepository.findUncheckedForDate(today)).thenReturn(List.of(booking));

        scheduler.liberateUncheckedSpots();

        verify(bookingRepository).updateEndDate(1L, today.minusDays(1));
        verify(bookingRepository).save(argThat(b ->
                b.startDate().equals(today.plusDays(1)) &&
                b.endDate().equals(today.plusDays(2)) &&
                b.spotId().equals("A01") &&
                b.email().equals("user@test.com")
        ));
    }

    @Test
    @DisplayName("does nothing when no unchecked bookings")
    void nothingWhenNoUnchecked() {
        when(bookingRepository.findUncheckedForDate(any())).thenReturn(List.of());

        scheduler.liberateUncheckedSpots();

        verify(bookingRepository, never()).deleteById(anyLong());
        verify(bookingRepository, never()).updateStartDate(anyLong(), any());
        verify(bookingRepository, never()).updateEndDate(anyLong(), any());
        verify(bookingRepository, never()).save(any());
    }
}
