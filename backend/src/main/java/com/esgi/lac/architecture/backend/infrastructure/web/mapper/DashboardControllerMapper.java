package com.esgi.lac.architecture.backend.infrastructure.web.mapper;

import com.esgi.lac.architecture.backend.domain.model.DashboardStats;
import com.esgi.lac.architecture.backend.domain.model.OccupationHistory;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.DashboardResponseDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.DashboardResponseDTO.DashboardStatsResponse;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.DashboardResponseDTO.ElectricDistributionResponse;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.DashboardResponseDTO.OccupationHistoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DashboardControllerMapper {

    public DashboardResponseDTO toResponse(DashboardStats stats) {
        return new DashboardResponseDTO(
                toStatsResponse(stats),
                toDistributionResponse(stats),
                toHistoryResponseList(stats.history())
        );
    }

    private DashboardStatsResponse toStatsResponse(DashboardStats stats) {
        return new DashboardStatsResponse(
                stats.totalSpots(),
                stats.occupiedSpots(),
                stats.distribution().availableElectric(),
                5.4,
                true
        );
    }

    private ElectricDistributionResponse toDistributionResponse(DashboardStats stats) {
        return new ElectricDistributionResponse(
                stats.distribution().occupiedElectric(),
                stats.distribution().availableElectric(),
                stats.distribution().classicSpots()
        );
    }

    private OccupationHistoryResponse toHistoryResponse(OccupationHistory h) {
        return new OccupationHistoryResponse(h.day(), h.totalOccupied(), h.electricOccupied());
    }

    private List<OccupationHistoryResponse> toHistoryResponseList(List<OccupationHistory> history) {
        return history.stream().map(this::toHistoryResponse).toList();
    }
}
