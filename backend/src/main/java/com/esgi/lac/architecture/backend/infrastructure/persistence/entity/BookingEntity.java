package com.esgi.lac.architecture.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "bookings")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String spotId;
    private String email;
    private String role;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean checkedIn;

}