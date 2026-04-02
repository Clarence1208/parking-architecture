package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.application.dto.BookingConfirmationMessage;
import com.esgi.lac.architecture.backend.application.service.MailService;
import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MailServiceTest {

    private final MailService mailService = new MailService();

    @Test
    @DisplayName("template contains recipient email and spot id")
    void shouldContainInformationsInTemplate() {
        BookingConfirmationMessage message = BookingConfirmationMessage.fromBooking(new Booking(
                1L, "A1", "test@test.fr", UserRole.EMPLOYEE,
                LocalDate.of(2026, 4, 10), LocalDate.of(2026, 4, 10), false
        ));

        String html = mailService.buildTemplate(message);

        assertThat(html).contains("test@test.fr");
        assertThat(html).contains("A1");
    }

    @Test
    @DisplayName("template contains start and end dates")
    void shouldContainDates() {
        BookingConfirmationMessage message = BookingConfirmationMessage.fromBooking(new Booking(
                1L, "B2", "user@example.com", UserRole.MANAGER,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 5), false
        ));

        String html = mailService.buildTemplate(message);

        assertThat(html).contains("2026-05-01");
        assertThat(html).contains("2026-05-05");
    }

    @Test
    @DisplayName("template contains HTML structure")
    void shouldContainHtmlStructure() {
        BookingConfirmationMessage message = BookingConfirmationMessage.fromBooking(new Booking(
                1L, "A1", "user@test.com", UserRole.EMPLOYEE,
                LocalDate.of(2026, 4, 10), LocalDate.of(2026, 4, 10), false
        ));

        String html = mailService.buildTemplate(message);

        assertThat(html).contains("<!DOCTYPE html>");
        assertThat(html).contains("Booking Confirmed");
    }
}
