package com.esgi.lac.architecture.backend.infrastructure.web.dto;

public record BookingRequestDTO(
        String spotId,
        String firstName,
        String lastName,
        String role,
        String bookingDate
) {}