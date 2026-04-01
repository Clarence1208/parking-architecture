package com.esgi.lac.architecture.backend.domain.model;

import java.time.LocalDate;

public record BookingSpotStatus(
        Long bookingId,
        String spotId,
        boolean occupied,
        String reservedBy,
        LocalDate date
) {}