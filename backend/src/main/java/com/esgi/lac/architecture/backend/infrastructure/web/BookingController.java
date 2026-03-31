package com.esgi.lac.architecture.backend.infrastructure.web;

import com.esgi.lac.architecture.backend.domain.usecase.BookingUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

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
        bookingUseCase.reserveSpot(
            (String) payload.get("spotId"),
            (String) payload.get("firstName"),
            (String) payload.get("lastName"),
            (Integer) payload.get("durationDays")
        );
        return ResponseEntity.ok().build();
    }
}