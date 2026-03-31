package com.esgi.lac.architecture.backend.domain.model;

import java.time.LocalDate;

public record BookingSpotStatus(
    String spotId,
    boolean occupied,
    String reservedBy,
    LocalDate date
) {}
