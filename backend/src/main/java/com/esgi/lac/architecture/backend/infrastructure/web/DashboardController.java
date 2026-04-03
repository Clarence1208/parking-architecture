package com.esgi.lac.architecture.backend.infrastructure.web;

import com.esgi.lac.architecture.backend.application.usecase.DashboardUseCase;
import com.esgi.lac.architecture.backend.domain.model.DashboardStats;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.DashboardResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard", description = "Statistics for parking usage")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardUseCase dashboardUseCase;

    public DashboardController(DashboardUseCase dashboardUseCase) {
        this.dashboardUseCase = dashboardUseCase;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponseDTO> getSummary() {
        DashboardStats domainStats = dashboardUseCase.getGlobalStats();

        DashboardResponseDTO response = new DashboardResponseDTO(
                new DashboardResponseDTO.DashboardStatsResponse(
                        domainStats.totalSpots(),
                        domainStats.occupiedSpots(),
                        domainStats.distribution().availableElectric(),
                        5.4,
                        true
                ),
                new DashboardResponseDTO.ElectricDistributionResponse(
                        domainStats.distribution().occupiedElectric(),
                        domainStats.distribution().availableElectric(),
                        domainStats.distribution().classicSpots()
                ),
                domainStats.history().stream()
                        .map(h -> new DashboardResponseDTO.OccupationHistoryResponse(
                                h.day(), h.totalOccupied(), h.electricOccupied()
                        ))
                        .toList()
        );

        return ResponseEntity.ok(response);
    }
}