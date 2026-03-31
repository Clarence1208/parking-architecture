package com.esgi.lac.architecture.backend.infrastructure.web;

import com.esgi.lac.architecture.backend.domain.model.Booking; // <--- AJOUTE ÇA
import com.esgi.lac.architecture.backend.domain.model.UserRole; // <--- AJOUTE ÇA
import com.esgi.lac.architecture.backend.domain.usecase.BookingUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@Tag(name = "Booking", description = "Parking booking operations")
@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private final BookingUseCase bookingUseCase;

    public BookingController(BookingUseCase bookingUseCase) {
        this.bookingUseCase = bookingUseCase;
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserve(@RequestBody Map<String, Object> payload) {
        // 1. On transforme le payload JSON en objet de domaine 'Booking'
        Booking booking = new Booking(
            (String) payload.get("spotId"),
            (String) payload.get("firstName"),
            (String) payload.get("lastName"),
            Integer.parseInt(payload.get("durationDays").toString()), // Sécurité sur le type
            UserRole.valueOf((String) payload.get("role")),           // On récupère le rôle choisi par l'utilisateur
            LocalDateTime.now()                                       // Date de création système
        );

        // 2. On passe l'objet au use case
        bookingUseCase.reserveSpot(booking);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/spots")
    public ResponseEntity<List<Map<String, Object>>> getSpots() {
        return ResponseEntity.ok(bookingUseCase.getAllSpots());
    }
}