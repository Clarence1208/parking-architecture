package com.esgi.lac.architecture.backend.infrastructure.web;

import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.application.usecase.BookingUseCase;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.BookingRequestDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.BookingResponseDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.CheckInRequestDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.CheckInResponseDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.UserBookingResponseDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.mapper.BookingControllerMapper;
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
    private final BookingControllerMapper bookingControllerMapper;

    public BookingController(BookingUseCase bookingUseCase, BookingControllerMapper bookingControllerMapper) {
        this.bookingUseCase = bookingUseCase;
        this.bookingControllerMapper = bookingControllerMapper;
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserve(@RequestBody BookingRequestDTO dto, Authentication authentication) {
        String email = authentication.getName();
        String roleString = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        Booking booking = bookingControllerMapper.toBooking(dto, email, UserRole.valueOf(roleString));

        bookingUseCase.reserveSpot(booking);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/spots")
    public ResponseEntity<List<BookingResponseDTO>> getSpots(@RequestParam(required = false) String date) {
        LocalDate targetDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();

        List<BookingResponseDTO> response = bookingControllerMapper.toBookingResponseList(
                bookingUseCase.getSpotsByDate(targetDate));

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
    public ResponseEntity<Void> cancel(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        String roleString = authentication.getAuthorities().iterator().next()
                .getAuthority().replace("ROLE_", "");
        UserRole role = UserRole.valueOf(roleString);

        bookingUseCase.cancelBooking(id, email, role);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check-in")
    public ResponseEntity<CheckInResponseDTO> checkIn(@RequestBody CheckInRequestDTO dto, Authentication authentication) {
        String email = authentication.getName();
        Booking confirmed = bookingUseCase.checkIn(dto.spotId(), email);

        return ResponseEntity.ok(bookingControllerMapper.toCheckInResponse(confirmed));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<UserBookingResponseDTO>> getMyBookings(Authentication authentication) {
        String email = authentication.getName();

        List<UserBookingResponseDTO> response = bookingControllerMapper.toUserBookingResponseList(
                bookingUseCase.getUserBookings(email));

        return ResponseEntity.ok(response);
    }
}
