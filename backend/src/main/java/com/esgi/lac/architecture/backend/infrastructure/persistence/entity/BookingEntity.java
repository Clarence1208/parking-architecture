package com.esgi.lac.architecture.backend.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class BookingEntity {
    @Id
    private String spotId;
    private String firstName;
    private String lastName;
    private int durationDays;
    private LocalDateTime createdAt;

    public BookingEntity() {}

    public BookingEntity(String spotId, String firstName, String lastName, int durationDays, LocalDateTime createdAt) {
        this.spotId = spotId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.durationDays = durationDays;
        this.createdAt = createdAt;
    }

    // Getters
    public String getSpotId() { return spotId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getDurationDays() { return durationDays; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setSpotId(String spotId) { this.spotId = spotId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}