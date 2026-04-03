package com.esgi.lac.architecture.backend.application.usecase;

import com.esgi.lac.architecture.backend.domain.model.DashboardStats;

public interface DashboardUseCase {
    DashboardStats getGlobalStats();
}