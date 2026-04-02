package com.esgi.lac.architecture.backend.infrastructure.web.dto;

import java.time.LocalDate;

public record CheckInResponseDTO(
        Long bookingId,
        String spotId,
        LocalDate startDate,
        LocalDate endDate,
        boolean checkedIn
) {}
