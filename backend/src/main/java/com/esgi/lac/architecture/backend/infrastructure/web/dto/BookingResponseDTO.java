package com.esgi.lac.architecture.backend.infrastructure.web.dto;

import java.time.LocalDate;

public record BookingResponseDTO(
        String spotId,
        boolean occupied,
        String reservedBy,
        LocalDate date
) {}
