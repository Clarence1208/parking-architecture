package com.esgi.lac.architecture.backend.application.service;

import com.esgi.lac.architecture.backend.application.repository.BookingRepository;
import com.esgi.lac.architecture.backend.application.repository.DashboardRepository;
import com.esgi.lac.architecture.backend.application.usecase.DashboardUseCase;
import com.esgi.lac.architecture.backend.domain.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DashboardService implements DashboardUseCase {

    private final BookingRepository bookingRepository;
    private final DashboardRepository dashboardRepository;

    public DashboardService(BookingRepository bookingRepository, DashboardRepository dashboardRepository) {
        this.bookingRepository = bookingRepository;
        this.dashboardRepository = dashboardRepository;
    }

    @Override
    public DashboardStats getGlobalStats() {
        LocalDate today = LocalDate.now();
        int totalSpots = dashboardRepository.countTotalSpots();
        int totalElectric = dashboardRepository.countElectricSpots();

        var currentBookings = bookingRepository.findAllOverlappingDate(today);
        int occupiedSpots = currentBookings.size();

        int occupiedElectric = (int) currentBookings.stream()
                .filter(b -> b.spotId().startsWith("A") || b.spotId().startsWith("F"))
                .count();

        ElectricDistribution distribution = new ElectricDistribution(
                occupiedElectric,
                totalElectric - occupiedElectric,
                totalSpots - totalElectric
        );

        return new DashboardStats(
                totalSpots,
                occupiedSpots,
                totalSpots - occupiedSpots,
                distribution,
                dashboardRepository.getLastSevenDaysHistory()
        );
    }
}