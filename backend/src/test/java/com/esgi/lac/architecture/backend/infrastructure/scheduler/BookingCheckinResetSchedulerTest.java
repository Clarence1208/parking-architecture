package com.esgi.lac.architecture.backend.infrastructure.scheduler;

import com.esgi.lac.architecture.backend.application.repository.BookingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingCheckinResetSchedulerTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingCheckinResetScheduler scheduler;

    @Test
    @DisplayName("delegates reset to repository with today's date")
    void delegatesToRepository() {
        LocalDate today = LocalDate.now();
        when(bookingRepository.resetCheckedInForMultiDayBookings(today)).thenReturn(3);

        scheduler.resetMultiDayCheckins();

        verify(bookingRepository).resetCheckedInForMultiDayBookings(today);
    }
}
