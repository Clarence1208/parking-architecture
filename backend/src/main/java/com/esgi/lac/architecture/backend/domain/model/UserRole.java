package com.esgi.lac.architecture.backend.domain.model;

public enum UserRole {
    EMPLOYEE(5),
    SECRETARY(5),
    MANAGER(30);

    private final int maxDays;

    UserRole(int maxDays) {
        this.maxDays = maxDays;
    }

    public int getMaxDays() {
        return maxDays;
    }
}