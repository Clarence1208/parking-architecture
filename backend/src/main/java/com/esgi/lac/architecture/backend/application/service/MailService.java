package com.esgi.lac.architecture.backend.application.service;

import com.esgi.lac.architecture.backend.application.dto.BookingConfirmationMessage;
import com.esgi.lac.architecture.backend.application.usecase.MailUseCase;
import org.springframework.stereotype.Service;

@Service
public class MailService implements MailUseCase {

    @Override
    public void sendConfirmation(BookingConfirmationMessage message) {
        String htmlMail = buildTemplate(message);
        System.out.println(htmlMail);
    }

    public String buildTemplate(BookingConfirmationMessage message) {
        return """
                <!DOCTYPE html>
                <html>
                <body>
                    <h1>Booking Confirmed</h1>
                    <p>Hello,</p>
                    <p>Your booking for <strong>%s</strong> parking spot has been confirmed.</p>
                    <p>From <strong>%s</strong> to <strong>%s</strong></p>
                    <p>Email sent to %s</p>
                </body>
                </html>
                """.formatted(
                message.getParkingSpotId(),
                message.getStartDate(),
                message.getEndDate(),
                message.getRecipientEmail()
        );
    }
}