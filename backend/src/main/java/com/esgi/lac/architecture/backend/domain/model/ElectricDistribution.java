package com.esgi.lac.architecture.backend.domain.model;

public record ElectricDistribution(
        int occupiedElectric,
        int availableElectric,
        int classicSpots
) {}