package com.esgi.lac.architecture.backend.application.repository;

import com.esgi.lac.architecture.backend.domain.model.OccupationHistory;
import java.util.List;

public interface DashboardRepository {
    int countTotalSpots();
    int countElectricSpots();
    List<OccupationHistory> getLastSevenDaysHistory();
}