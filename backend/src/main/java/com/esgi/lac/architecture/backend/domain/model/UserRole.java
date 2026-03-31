package com.esgi.lac.architecture.backend.domain.model;

import lombok.Getter;

@Getter
public enum UserRole {
    EMPLOYEE(5),
    SECRETARY(5),
    MANAGER(30);

    private final int maxNumberOfBookingDays;

    UserRole(int maxNumberOfBookingDays) {
        this.maxNumberOfBookingDays = maxNumberOfBookingDays;
    }
}