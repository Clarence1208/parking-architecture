package com.esgi.lac.architecture.backend.infrastructure.web;

import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.application.usecase.BookingUseCase;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.BookingRequestDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.BookingResponseDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.CheckInRequestDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.CheckInResponseDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.UserBookingResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;

@Tag(name = "Booking", description = "Parking booking operations")
@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private final BookingUseCase bookingUseCase;

    public BookingController(BookingUseCase bookingUseCase) {
        this.bookingUseCase = bookingUseCase;
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@RequestBody BookingRequestDTO dto, Authentication authentication) {
        try {
            String email = authentication.getName();
            String roleString = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

            Booking booking = new Booking(
                    null,
                    dto.spotId(),
                    email,
                    UserRole.valueOf(roleString),
                    LocalDate.parse(dto.startDate()),
                    LocalDate.parse(dto.endDate()),
                    false
            );

            bookingUseCase.reserveSpot(booking);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }
    }

    public record ErrorResponse(String message) {}
    @GetMapping("/spots")
    public ResponseEntity<List<BookingResponseDTO>> getSpots(@RequestParam(required = false) String date) {
        LocalDate targetDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();

        List<BookingResponseDTO> response = bookingUseCase.getSpotsByDate(targetDate)
                .stream()
                .map(spot -> new BookingResponseDTO(
                        spot.bookingId(),
                        spot.spotId(),
                        spot.occupied(),
                        spot.reservedBy(),
                        spot.date()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    public record RemainingDaysResponse(long remainingDays, int maxDays, String role) {}

    @GetMapping("/remaining-days")
    public ResponseEntity<RemainingDaysResponse> getRemainingDays(Authentication authentication) {
        String email = authentication.getName();
        String roleString = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        UserRole role = UserRole.valueOf(roleString);

        long remaining = bookingUseCase.getRemainingDays(email, role);

        return ResponseEntity.ok(new RemainingDaysResponse(remaining, role.getMaxNumberOfBookingDays(), roleString));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id, Authentication authentication) {
        try {
            String email = authentication.getName();
            String roleString = authentication.getAuthorities().iterator().next()
                    .getAuthority().replace("ROLE_", "");
            UserRole role = UserRole.valueOf(roleString);

            bookingUseCase.cancelBooking(id, email, role);

            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }
    }

    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestBody CheckInRequestDTO dto, Authentication authentication) {
        try {
            String email = authentication.getName();
            Booking confirmed = bookingUseCase.checkIn(dto.spotId(), email);

            CheckInResponseDTO response = new CheckInResponseDTO(
                    confirmed.id(),
                    confirmed.spotId(),
                    confirmed.startDate(),
                    confirmed.endDate(),
                    confirmed.checkedIn()
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<UserBookingResponseDTO>> getMyBookings(Authentication authentication) {

        String email = authentication.getName();

        List<UserBookingResponseDTO> response = bookingUseCase.getUserBookings(email)
                .stream()
                .map(b -> new UserBookingResponseDTO(
                        b.id(),
                        b.spotId(),
                        b.startDate(),
                        b.endDate(),
                        b.checkedIn()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}