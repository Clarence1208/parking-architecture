package com.esgi.lac.architecture.backend.infrastructure.scheduler;

import com.esgi.lac.architecture.backend.application.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class BookingCheckinResetScheduler {

    private static final Logger log = LoggerFactory.getLogger(BookingCheckinResetScheduler.class);

    private final BookingRepository bookingRepository;

    public BookingCheckinResetScheduler(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetMultiDayCheckins() {
        LocalDate today = LocalDate.now();
        int resetCount = bookingRepository.resetCheckedInForMultiDayBookings(today);
        log.info("Reset check-in for {} multi-day booking(s) on {}", resetCount, today);
    }
}
