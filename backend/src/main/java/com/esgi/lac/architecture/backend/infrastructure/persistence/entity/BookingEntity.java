package com.esgi.lac.architecture.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "bookings")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String spotId;
    private String firstName;
    private String lastName;
    private LocalDate bookingDate;

    public BookingEntity() {
    }

    public BookingEntity(String spotId, String firstName, String lastName, LocalDate bookingDate) {
        this.spotId = spotId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bookingDate = bookingDate;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getSpotId() {
        return spotId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    //SEtters

    public void setId(Long id) {
        this.id = id;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }
}