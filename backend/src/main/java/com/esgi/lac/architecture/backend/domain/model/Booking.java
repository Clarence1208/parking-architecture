package com.esgi.lac.architecture.backend.domain.model;

import java.time.LocalDate;

public record Booking(
    String spotId,
    String firstName,
    String lastName,
    UserRole role,
    LocalDate bookingDate
) {}