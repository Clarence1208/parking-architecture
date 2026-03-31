package com.esgi.lac.architecture.backend.infrastructure.web.dto;

import java.time.LocalDate;

public record BookingResponseDTO(
        String id,          // Le spotId (ex: "A01")
        boolean isOccupied,
        String reservedBy,
        LocalDate date
) {}
