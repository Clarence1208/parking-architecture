package com.esgi.lac.architecture.backend.domain.exception;

public class BookingQuotaExceededException extends DomainException {

    public BookingQuotaExceededException(String message) {
        super(message);
    }
}
