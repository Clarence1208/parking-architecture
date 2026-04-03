package com.esgi.lac.architecture.backend.infrastructure.web;

import com.esgi.lac.architecture.backend.application.usecase.DashboardUseCase;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.DashboardResponseDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.mapper.DashboardControllerMapper;
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
    private final DashboardControllerMapper dashboardControllerMapper;

    public DashboardController(DashboardUseCase dashboardUseCase, DashboardControllerMapper dashboardControllerMapper) {
        this.dashboardUseCase = dashboardUseCase;
        this.dashboardControllerMapper = dashboardControllerMapper;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponseDTO> getSummary() {
        return ResponseEntity.ok(dashboardControllerMapper.toResponse(dashboardUseCase.getGlobalStats()));
    }
}