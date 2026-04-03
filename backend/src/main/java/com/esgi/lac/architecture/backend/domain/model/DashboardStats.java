package com.esgi.lac.architecture.backend.domain.model;

import java.util.List;

public record DashboardStats(
        int totalSpots,
        int occupiedSpots,
        int availableSpots,
        ElectricDistribution distribution,
        List<OccupationHistory> history
) {}
