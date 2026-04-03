package com.esgi.lac.architecture.backend.infrastructure.web.dto;

import java.util.List;

public record DashboardResponseDTO(
        DashboardStatsResponse stats,
        ElectricDistributionResponse distribution,
        List<OccupationHistoryResponse> history
) {
    public record DashboardStatsResponse(
            int totalSpots,
            int occupiedSpots,
            int availableElectricSpots,
            double occupancyTrend,
            boolean isTrendPositive
    ) {}

    public record ElectricDistributionResponse(
            int occupiedElectric,
            int availableElectric,
            int classicSpots
    ) {}

    public record OccupationHistoryResponse(
            String day,
            int totalOccupied,
            int electricOccupied
    ) {}
}