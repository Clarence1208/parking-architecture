package com.esgi.lac.architecture.backend.application.repository;

import com.esgi.lac.architecture.backend.application.dto.BookingConfirmationMessage;

public interface BookingQueuePort {
    void publish(BookingConfirmationMessage message);
}
