package com.esgi.lac.architecture.backend.infrastructure.scheduler;

import com.esgi.lac.architecture.backend.application.repository.BookingRepository;
import com.esgi.lac.architecture.backend.domain.model.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class BookingNoShowScheduler {

    private static final Logger log = LoggerFactory.getLogger(BookingNoShowScheduler.class);

    private final BookingRepository bookingRepository;

    public BookingNoShowScheduler(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Scheduled(cron = "0 0 11 * * *")
    @Transactional
    public void liberateUncheckedSpots() {
        LocalDate today = LocalDate.now();
        List<Booking> unchecked = bookingRepository.findUncheckedForDate(today);

        int liberated = 0;
        for (Booking booking : unchecked) {
            boolean startsToday = booking.startDate().equals(today);
            boolean endsToday = booking.endDate().equals(today);

            if (startsToday && endsToday) {
                bookingRepository.deleteById(booking.id());
            } else if (startsToday) {
                bookingRepository.updateStartDate(booking.id(), today.plusDays(1));
            } else if (endsToday) {
                bookingRepository.updateEndDate(booking.id(), today.minusDays(1));
            } else {
                // Today is in the middle of a multi-day booking: split around today
                LocalDate originalEnd = booking.endDate();
                bookingRepository.updateEndDate(booking.id(), today.minusDays(1));
                Booking remainder = new Booking(
                        null, booking.spotId(), booking.email(), booking.role(),
                        today.plusDays(1), originalEnd, false
                );
                bookingRepository.save(remainder);
            }
            liberated++;
        }

        log.info("Liberated {} unchecked spot(s) at 11:00 on {}", liberated, today);
    }
}
