package com.esgi.lac.architecture.backend.domain.usecase;

import java.util.List;
import java.util.Map;

public interface BookingUseCase {
    void reserveSpot(String spotId, String firstName, String lastName, int durationDays);
    List<Map<String, Object>> getAllSpots();
}