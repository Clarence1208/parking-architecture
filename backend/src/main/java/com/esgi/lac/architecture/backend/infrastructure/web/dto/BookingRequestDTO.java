package com.esgi.lac.architecture.backend.infrastructure.web.dto;

public record BookingRequestDTO(
        String spotId,
        String bookingDate
) {}