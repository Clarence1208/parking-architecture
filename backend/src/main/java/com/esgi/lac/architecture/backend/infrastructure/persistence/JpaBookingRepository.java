package com.esgi.lac.architecture.backend.infrastructure.persistence;

import com.esgi.lac.architecture.backend.infrastructure.persistence.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaBookingRepository extends JpaRepository<BookingEntity, Long> {
    // Trouver toutes les réservations pour un jour précis
    List<BookingEntity> findAllByBookingDate(LocalDate date);

    // Vérifier si une place précise est prise à une date précise
    Optional<BookingEntity> findBySpotIdAndBookingDate(String spotId, LocalDate date);

    // Vérifier si un utilisateur a déjà une réservation pour une date précise
    boolean existsByEmailAndBookingDate(String email, LocalDate date);

    // Compter les réservations à venir d'un utilisateur (pour le quota)
    long countByEmailAndBookingDateGreaterThanEqual(String email, LocalDate fromDate);
}