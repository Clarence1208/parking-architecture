package com.esgi.lac.architecture.backend.domain.usecase;
import com.esgi.lac.architecture.backend.domain.model.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BookingUseCase {
    void reserveSpot(Booking booking);

    List<Map<String, Object>> getSpotsByDate(LocalDate date);
    List<Map<String, Object>> getAllSpots();
}