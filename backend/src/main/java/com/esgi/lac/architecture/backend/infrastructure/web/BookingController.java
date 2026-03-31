package com.esgi.lac.architecture.backend.infrastructure.web;

import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.application.usecase.BookingUseCase;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.BookingRequestDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.BookingResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<Void> reserve(@RequestBody BookingRequestDTO dto) {
        Booking booking = new Booking(
                dto.spotId(),
                dto.firstName(),
                dto.lastName(),
                UserRole.valueOf(dto.role()),
                LocalDate.parse(dto.bookingDate())
        );

        bookingUseCase.reserveSpot(booking);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/spots")
    public ResponseEntity<List<BookingResponseDTO>> getSpots(@RequestParam(required = false) String date) {
        LocalDate targetDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();

        List<BookingResponseDTO> response = bookingUseCase.getSpotsByDate(targetDate)
                .stream()
                .map(spot -> new BookingResponseDTO(
                        spot.spotId(),
                        spot.occupied(),
                        spot.reservedBy(),
                        spot.date()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}