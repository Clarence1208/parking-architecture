package com.esgi.lac.architecture.backend.infrastructure.persistence;

import com.esgi.lac.architecture.backend.application.repository.DashboardRepository;
import com.esgi.lac.architecture.backend.domain.model.OccupationHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DashboardRepositoryAdapter implements DashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    public DashboardRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int countTotalSpots() {
        return 60;
    }

    @Override
    public int countElectricSpots() {
        return 20;
    }

    @Override
    public List<OccupationHistory> getLastSevenDaysHistory() {
        List<OccupationHistory> history = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusDays(i);
            String label = d.getDayOfWeek().name().substring(0, 3);
            history.add(new OccupationHistory(label, 35 + i, 8 + (i % 3)));
        }
        return history;
    }
}