package com.esgi.lac.architecture.backend.infrastructure.web.dto;

import java.time.LocalDate;

public record UserBookingResponseDTO(
        Long id,
        String spotId,
        LocalDate startDate,
        LocalDate endDate
){}