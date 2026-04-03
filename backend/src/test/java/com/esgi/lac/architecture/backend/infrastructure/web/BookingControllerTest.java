package com.esgi.lac.architecture.backend.infrastructure.web;

import com.esgi.lac.architecture.backend.application.exception.BookingNotFoundException;
import com.esgi.lac.architecture.backend.application.exception.CheckInNotFoundException;
import com.esgi.lac.architecture.backend.application.exception.UnauthorizedCancellationException;
import com.esgi.lac.architecture.backend.application.usecase.BookingUseCase;
import com.esgi.lac.architecture.backend.domain.exception.BookingOverlapException;
import com.esgi.lac.architecture.backend.domain.exception.BookingQuotaExceededException;
import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.BookingSpotStatus;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.infrastructure.web.mapper.BookingControllerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingUseCase bookingUseCase;

    private MockMvc mockMvc;

    private Authentication employeeAuth;
    private Authentication secretaryAuth;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BookingController(bookingUseCase, new BookingControllerMapper()))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        employeeAuth = new UsernamePasswordAuthenticationToken(
                "user@test.com", null,
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))
        );
        secretaryAuth = new UsernamePasswordAuthenticationToken(
                "secretary@test.com", null,
                List.of(new SimpleGrantedAuthority("ROLE_SECRETARY"))
        );
    }

    @Nested
    @DisplayName("POST /api/booking/reserve")
    class Reserve {

        @Test
        @DisplayName("returns 200 on successful reservation")
        void successfulReservation() throws Exception {
            LocalDate start = LocalDate.now().plusDays(1);
            LocalDate end = LocalDate.now().plusDays(1);
            String json = """
                    {"spotId": "A01", "startDate": "%s", "endDate": "%s"}
                    """.formatted(start, end);

            mockMvc.perform(post("/api/booking/reserve")
                            .principal(employeeAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk());

            verify(bookingUseCase).reserveSpot(any(Booking.class));
        }

        @Test
        @DisplayName("returns 400 when quota is exceeded")
        void returns400OnQuotaExceeded() throws Exception {
            doThrow(new BookingQuotaExceededException("Quota exceeded"))
                    .when(bookingUseCase).reserveSpot(any());

            String json = """
                    {"spotId": "A01", "startDate": "%s", "endDate": "%s"}
                    """.formatted(LocalDate.now().plusDays(1), LocalDate.now().plusDays(1));

            mockMvc.perform(post("/api/booking/reserve")
                            .principal(employeeAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Quota exceeded"));
        }

        @Test
        @DisplayName("returns 400 when spot overlaps")
        void returns400OnOverlap() throws Exception {
            doThrow(new BookingOverlapException("Spot already taken"))
                    .when(bookingUseCase).reserveSpot(any());

            String json = """
                    {"spotId": "A01", "startDate": "%s", "endDate": "%s"}
                    """.formatted(LocalDate.now().plusDays(1), LocalDate.now().plusDays(1));

            mockMvc.perform(post("/api/booking/reserve")
                            .principal(employeeAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Spot already taken"));
        }
    }

    @Nested
    @DisplayName("GET /api/booking/spots")
    class GetSpots {

        @Test
        @DisplayName("returns spots for a specific date")
        void returnsSpotsForDate() throws Exception {
            LocalDate date = LocalDate.of(2026, 5, 1);
            List<BookingSpotStatus> spots = List.of(
                    new BookingSpotStatus(1L, "A01", true, "user@test.com", date, false)
            );
            when(bookingUseCase.getSpotsByDate(date)).thenReturn(spots);

            mockMvc.perform(get("/api/booking/spots").param("date", "2026-05-01"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].spotId").value("A01"))
                    .andExpect(jsonPath("$[0].occupied").value(true))
                    .andExpect(jsonPath("$[0].reservedBy").value("user@test.com"));
        }

        @Test
        @DisplayName("returns spots for today when no date specified")
        void returnsSpotsForToday() throws Exception {
            when(bookingUseCase.getSpotsByDate(LocalDate.now())).thenReturn(List.of());

            mockMvc.perform(get("/api/booking/spots"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/booking/remaining-days")
    class RemainingDays {

        @Test
        @DisplayName("returns remaining days for authenticated user")
        void returnsRemainingDays() throws Exception {
            when(bookingUseCase.getRemainingDays("user@test.com", UserRole.EMPLOYEE)).thenReturn(3L);

            mockMvc.perform(get("/api/booking/remaining-days").principal(employeeAuth))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.remainingDays").value(3))
                    .andExpect(jsonPath("$.maxDays").value(5))
                    .andExpect(jsonPath("$.role").value("EMPLOYEE"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/booking/{id}")
    class CancelBooking {

        @Test
        @DisplayName("returns 204 on successful cancellation")
        void successfulCancellation() throws Exception {
            mockMvc.perform(delete("/api/booking/1").principal(employeeAuth))
                    .andExpect(status().isNoContent());

            verify(bookingUseCase).cancelBooking(1L, "user@test.com", UserRole.EMPLOYEE);
        }

        @Test
        @DisplayName("returns 403 when cancellation denied")
        void returns403WhenDenied() throws Exception {
            doThrow(new UnauthorizedCancellationException("Not allowed"))
                    .when(bookingUseCase).cancelBooking(anyLong(), anyString(), any());

            mockMvc.perform(delete("/api/booking/1").principal(employeeAuth))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Not allowed"));
        }

        @Test
        @DisplayName("returns 404 when booking not found")
        void returns404WhenNotFound() throws Exception {
            doThrow(new BookingNotFoundException("Booking not found"))
                    .when(bookingUseCase).cancelBooking(anyLong(), anyString(), any());

            mockMvc.perform(delete("/api/booking/99").principal(employeeAuth))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Booking not found"));
        }
    }

    @Nested
    @DisplayName("POST /api/booking/check-in")
    class CheckInEndpoint {

        @Test
        @DisplayName("returns check-in confirmation on success")
        void successfulCheckIn() throws Exception {
            Booking confirmed = new Booking(1L, "A01", "user@test.com", UserRole.EMPLOYEE,
                    LocalDate.now(), LocalDate.now(), true);
            when(bookingUseCase.checkIn("A01", "user@test.com")).thenReturn(confirmed);

            mockMvc.perform(post("/api/booking/check-in")
                            .principal(employeeAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"spotId": "A01"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.spotId").value("A01"))
                    .andExpect(jsonPath("$.checkedIn").value(true));
        }

        @Test
        @DisplayName("returns 404 when check-in booking not found")
        void returns404OnNotFound() throws Exception {
            when(bookingUseCase.checkIn(anyString(), anyString()))
                    .thenThrow(new CheckInNotFoundException("No reservation found"));

            mockMvc.perform(post("/api/booking/check-in")
                            .principal(employeeAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"spotId": "Z99"}
                                    """))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("No reservation found"));
        }
    }

    @Nested
    @DisplayName("GET /api/booking/my-bookings")
    class MyBookings {

        @Test
        @DisplayName("returns bookings for authenticated user")
        void returnsUserBookings() throws Exception {
            List<Booking> bookings = List.of(
                    new Booking(1L, "A01", "user@test.com", UserRole.EMPLOYEE,
                            LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), false)
            );
            when(bookingUseCase.getUserBookings("user@test.com")).thenReturn(bookings);

            mockMvc.perform(get("/api/booking/my-bookings").principal(employeeAuth))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].spotId").value("A01"))
                    .andExpect(jsonPath("$[0].id").value(1));
        }

        @Test
        @DisplayName("returns empty list when user has no bookings")
        void returnsEmptyList() throws Exception {
            when(bookingUseCase.getUserBookings("user@test.com")).thenReturn(List.of());

            mockMvc.perform(get("/api/booking/my-bookings").principal(employeeAuth))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }
}
