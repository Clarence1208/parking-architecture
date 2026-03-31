package com.esgi.lac.architecture.backend.infrastructure.persistence;

import com.esgi.lac.architecture.backend.infrastructure.persistence.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaBookingRepository extends JpaRepository<BookingEntity, String> {
}