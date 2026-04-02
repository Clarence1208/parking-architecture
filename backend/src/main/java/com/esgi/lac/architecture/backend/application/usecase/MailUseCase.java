package com.esgi.lac.architecture.backend.application.usecase;

import com.esgi.lac.architecture.backend.application.dto.BookingConfirmationMessage;

public interface MailUseCase {
    void sendConfirmation(BookingConfirmationMessage message);
}