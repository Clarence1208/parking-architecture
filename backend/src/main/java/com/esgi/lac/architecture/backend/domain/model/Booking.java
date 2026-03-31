package com.esgi.lac.architecture.backend.domain.model;

import java.time.LocalDateTime;

public record Booking(
    String spotId,
    String firstName,
    String lastName,
    int durationDays,
    UserRole role,
    LocalDateTime createdAt
) {}