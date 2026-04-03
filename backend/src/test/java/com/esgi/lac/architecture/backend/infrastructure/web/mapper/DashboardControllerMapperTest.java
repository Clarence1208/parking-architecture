package com.esgi.lac.architecture.backend.infrastructure.web.mapper;

import com.esgi.lac.architecture.backend.domain.model.DashboardStats;
import com.esgi.lac.architecture.backend.domain.model.ElectricDistribution;
import com.esgi.lac.architecture.backend.domain.model.OccupationHistory;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.DashboardResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DashboardControllerMapperTest {

    private final DashboardControllerMapper mapper = new DashboardControllerMapper();

    private DashboardStats makeSampleStats() {
        ElectricDistribution distribution = new ElectricDistribution(8, 12, 40);
        List<OccupationHistory> history = List.of(
                new OccupationHistory("MON", 35, 8),
                new OccupationHistory("TUE", 40, 10),
                new OccupationHistory("WED", 38, 9)
        );
        return new DashboardStats(60, 42, 18, distribution, history);
    }

    @Nested
    @DisplayName("toResponse")
    class ToResponse {

        @Test
        @DisplayName("maps stats section correctly")
        void mapsStats() {
            DashboardResponseDTO response = mapper.toResponse(makeSampleStats());

            assertThat(response.stats().totalSpots()).isEqualTo(60);
            assertThat(response.stats().occupiedSpots()).isEqualTo(42);
            assertThat(response.stats().availableElectricSpots()).isEqualTo(12);
            assertThat(response.stats().occupancyTrend()).isEqualTo(5.4);
            assertThat(response.stats().isTrendPositive()).isTrue();
        }

        @Test
        @DisplayName("maps electric distribution correctly")
        void mapsDistribution() {
            DashboardResponseDTO response = mapper.toResponse(makeSampleStats());

            assertThat(response.distribution().occupiedElectric()).isEqualTo(8);
            assertThat(response.distribution().availableElectric()).isEqualTo(12);
            assertThat(response.distribution().classicSpots()).isEqualTo(40);
        }

        @Test
        @DisplayName("maps occupation history correctly")
        void mapsHistory() {
            DashboardResponseDTO response = mapper.toResponse(makeSampleStats());

            assertThat(response.history()).hasSize(3);
            assertThat(response.history().get(0).day()).isEqualTo("MON");
            assertThat(response.history().get(0).totalOccupied()).isEqualTo(35);
            assertThat(response.history().get(0).electricOccupied()).isEqualTo(8);
            assertThat(response.history().get(2).day()).isEqualTo("WED");
        }

        @Test
        @DisplayName("handles empty history list")
        void handlesEmptyHistory() {
            ElectricDistribution distribution = new ElectricDistribution(0, 20, 40);
            DashboardStats stats = new DashboardStats(60, 0, 60, distribution, List.of());

            DashboardResponseDTO response = mapper.toResponse(stats);

            assertThat(response.history()).isEmpty();
            assertThat(response.stats().occupiedSpots()).isZero();
        }

        @Test
        @DisplayName("preserves all history entries in order")
        void preservesHistoryOrder() {
            DashboardResponseDTO response = mapper.toResponse(makeSampleStats());

            List<String> days = response.history().stream()
                    .map(DashboardResponseDTO.OccupationHistoryResponse::day)
                    .toList();
            assertThat(days).containsExactly("MON", "TUE", "WED");
        }
    }
}
