package com.esgi.lac.architecture.backend.domain.model;

public record OccupationHistory(
        String day,
        int totalOccupied,
        int electricOccupied
) {}