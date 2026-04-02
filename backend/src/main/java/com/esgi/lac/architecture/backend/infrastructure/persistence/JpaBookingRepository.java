package com.esgi.lac.architecture.backend.infrastructure.persistence;

import com.esgi.lac.architecture.backend.infrastructure.persistence.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface JpaBookingRepository extends JpaRepository<BookingEntity, Long> {

    // Trouver toutes les réservations dont l'intervalle chevauche une date donnée
    @Query("SELECT b FROM BookingEntity b WHERE b.startDate <= :date AND b.endDate >= :date")
    List<BookingEntity> findAllOverlappingDate(@Param("date") LocalDate date);

    // Vérifier si une place a un booking qui chevauche un intervalle donné
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BookingEntity b " +
           "WHERE b.spotId = :spotId AND b.startDate <= :endDate AND b.endDate >= :startDate")
    boolean existsOverlappingSpotBooking(@Param("spotId") String spotId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    // Vérifier si un utilisateur a un booking qui chevauche un intervalle donné
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BookingEntity b " +
           "WHERE b.email = :email AND b.startDate <= :endDate AND b.endDate >= :startDate")
    boolean existsOverlappingUserBooking(@Param("email") String email,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    // Trouver tous les bookings à venir d'un utilisateur (pour calcul du quota en jours)
    @Query("SELECT b FROM BookingEntity b WHERE b.email = :email AND b.endDate >= :fromDate")
    List<BookingEntity> findUpcomingByUser(@Param("email") String email, @Param("fromDate") LocalDate fromDate);

    @Query("SELECT b FROM BookingEntity b WHERE b.email = :email ORDER BY b.startDate ASC")
    List<BookingEntity> findAllByEmail(@Param("email") String email);
}