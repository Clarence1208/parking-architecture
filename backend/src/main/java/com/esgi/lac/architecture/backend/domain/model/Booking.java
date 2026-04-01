package com.esgi.lac.architecture.backend.domain.model;

import java.time.LocalDate;

public record Booking(
    String spotId,
    String email,
    UserRole role,
    LocalDate startDate,
    LocalDate endDate
) {}