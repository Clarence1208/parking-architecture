package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.application.dto.BookingConfirmationMessage;
import com.esgi.lac.architecture.backend.application.service.MailService;
import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MailServiceTest {

    private final MailService mailService = new MailService();

    @Test
    void shouldContainInformationsInTemplate() {
        BookingConfirmationMessage message = BookingConfirmationMessage.fromBooking(new Booking(
                1L,
                "A1",
                "test@test.Fr",
                UserRole.EMPLOYEE,
                LocalDate.of(2026, 4, 10),
                LocalDate.of(2026, 4, 10)
        ));

        String html = mailService.buildTemplate(message);

        assertThat(html).contains("test@test.Fr");
        assertThat(html).contains("A1");
    }
}