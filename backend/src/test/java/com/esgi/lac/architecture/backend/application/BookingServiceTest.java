package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.application.repository.BookingQueuePort;
import com.esgi.lac.architecture.backend.application.repository.BookingRepository;
import com.esgi.lac.architecture.backend.application.service.BookingService;
import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.BookingSpotStatus;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository repository;

    @Mock
    private BookingQueuePort bookingPublisher;

    @InjectMocks
    private BookingService bookingService;

    private Booking makeBooking(String spotId, String email, UserRole role, LocalDate start, LocalDate end) {
        return new Booking(null, spotId, email, role, start, end, false);
    }

    @Nested
    @DisplayName("reserveSpot")
    class ReserveSpot {

        @Test
        @DisplayName("rejects bookings in the past")
        void rejectsBookingInThePast() {
            LocalDate pastDate = LocalDate.now().minusDays(1);
            Booking booking = makeBooking("A01", "user@test.com", UserRole.EMPLOYEE, pastDate, pastDate);

            assertThatThrownBy(() -> bookingService.reserveSpot(booking))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("rejects bookings where end date is before start date")
        void rejectsEndBeforeStart() {
            LocalDate start = LocalDate.now().plusDays(3);
            LocalDate end = LocalDate.now().plusDays(1);
            Booking booking = makeBooking("A01", "user@test.com", UserRole.EMPLOYEE, start, end);

            assertThatThrownBy(() -> bookingService.reserveSpot(booking))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("rejects when user already has overlapping booking")
        void rejectsOverlappingUserBooking() {
            LocalDate start = LocalDate.now().plusDays(1);
            LocalDate end = LocalDate.now().plusDays(1);
            when(repository.existsOverlappingUserBooking("alan@test.com", start, end)).thenReturn(true);

            Booking booking = makeBooking("A01", "alan@test.com", UserRole.EMPLOYEE, start, end);

            assertThatThrownBy(() -> bookingService.reserveSpot(booking))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("rejects when spot is already booked for the interval")
        void rejectsOverlappingSpotBooking() {
            LocalDate start = LocalDate.now().plusDays(1);
            LocalDate end = LocalDate.now().plusDays(1);
            when(repository.existsOverlappingUserBooking("alan@test.com", start, end)).thenReturn(false);
            when(repository.existsOverlappingSpotBooking("A01", start, end)).thenReturn(true);

            Booking booking = makeBooking("A01", "alan@test.com", UserRole.EMPLOYEE, start, end);

            assertThatThrownBy(() -> bookingService.reserveSpot(booking))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("employee cannot exceed 5-day quota")
        void employeeCannotExceedQuota() {
            LocalDate start = LocalDate.now().plusDays(1);
            LocalDate end = LocalDate.now().plusDays(1);

            when(repository.existsOverlappingUserBooking("alan@test.com", start, end)).thenReturn(false);
            when(repository.existsOverlappingSpotBooking("A01", start, end)).thenReturn(false);
            when(repository.countUpcomingDaysByUser("alan@test.com", LocalDate.now())).thenReturn(5L);

            Booking booking = makeBooking("A01", "alan@test.com", UserRole.EMPLOYEE, start, end);

            assertThatThrownBy(() -> bookingService.reserveSpot(booking))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("employee cannot exceed quota with multi-day range")
        void employeeCannotExceedQuotaMultiDay() {
            LocalDate start = LocalDate.now().plusDays(1);
            LocalDate end = LocalDate.now().plusDays(3);

            when(repository.existsOverlappingUserBooking("alan@test.com", start, end)).thenReturn(false);
            when(repository.existsOverlappingSpotBooking("A01", start, end)).thenReturn(false);
            when(repository.countUpcomingDaysByUser("alan@test.com", LocalDate.now())).thenReturn(3L);

            Booking booking = makeBooking("A01", "alan@test.com", UserRole.EMPLOYEE, start, end);

            assertThatThrownBy(() -> bookingService.reserveSpot(booking))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("manager can reserve up to 30 days")
        void managerCanReserveUpTo30Days() {
            LocalDate start = LocalDate.now().plusDays(1);
            LocalDate end = LocalDate.now().plusDays(1);

            when(repository.existsOverlappingUserBooking("chef@test.com", start, end)).thenReturn(false);
            when(repository.existsOverlappingSpotBooking("B02", start, end)).thenReturn(false);
            when(repository.countUpcomingDaysByUser("chef@test.com", LocalDate.now())).thenReturn(29L);

            Booking booking = makeBooking("B02", "chef@test.com", UserRole.MANAGER, start, end);

            bookingService.reserveSpot(booking);

            verify(repository).save(any());
            verify(bookingPublisher).publish(any());
        }

        @Test
        @DisplayName("employee can reserve a multi-day range within quota")
        void employeeCanReserveMultiDayRange() {
            LocalDate start = LocalDate.now().plusDays(1);
            LocalDate end = LocalDate.now().plusDays(3);

            when(repository.existsOverlappingUserBooking("alan@test.com", start, end)).thenReturn(false);
            when(repository.existsOverlappingSpotBooking("A01", start, end)).thenReturn(false);
            when(repository.countUpcomingDaysByUser("alan@test.com", LocalDate.now())).thenReturn(0L);

            Booking booking = makeBooking("A01", "alan@test.com", UserRole.EMPLOYEE, start, end);

            bookingService.reserveSpot(booking);

            verify(repository).save(any());
            verify(bookingPublisher).publish(any());
        }

        @Test
        @DisplayName("publishes confirmation message to queue after saving")
        void publishesToQueueAfterSave() {
            LocalDate start = LocalDate.now().plusDays(1);
            LocalDate end = LocalDate.now().plusDays(1);

            when(repository.existsOverlappingUserBooking("user@test.com", start, end)).thenReturn(false);
            when(repository.existsOverlappingSpotBooking("A01", start, end)).thenReturn(false);
            when(repository.countUpcomingDaysByUser("user@test.com", LocalDate.now())).thenReturn(0L);

            Booking booking = makeBooking("A01", "user@test.com", UserRole.EMPLOYEE, start, end);

            bookingService.reserveSpot(booking);

            verify(bookingPublisher).publish(argThat(msg ->
                    "user@test.com".equals(msg.getRecipientEmail()) &&
                    "A01".equals(msg.getParkingSpotId())
            ));
        }
    }

    @Nested
    @DisplayName("getSpotsByDate")
    class GetSpotsByDate {

        @Test
        @DisplayName("returns spot statuses for the given date")
        void returnsSpotsForDate() {
            LocalDate date = LocalDate.now();
            Booking b1 = new Booking(1L, "A01", "user1@test.com", UserRole.EMPLOYEE, date, date, true);
            Booking b2 = new Booking(2L, "B02", "user2@test.com", UserRole.MANAGER, date, date, false);
            when(repository.findAllOverlappingDate(date)).thenReturn(List.of(b1, b2));

            List<BookingSpotStatus> spots = bookingService.getSpotsByDate(date);

            assertThat(spots).hasSize(2);
            assertThat(spots.get(0).spotId()).isEqualTo("A01");
            assertThat(spots.get(0).occupied()).isTrue();
            assertThat(spots.get(0).checkedIn()).isTrue();
            assertThat(spots.get(1).spotId()).isEqualTo("B02");
            assertThat(spots.get(1).checkedIn()).isFalse();
        }

        @Test
        @DisplayName("returns empty list when no bookings for date")
        void returnsEmptyWhenNone() {
            when(repository.findAllOverlappingDate(any())).thenReturn(List.of());

            List<BookingSpotStatus> spots = bookingService.getSpotsByDate(LocalDate.now());

            assertThat(spots).isEmpty();
        }
    }

    @Nested
    @DisplayName("getRemainingDays")
    class GetRemainingDays {

        @Test
        @DisplayName("returns correct remaining days for employee")
        void correctRemainingForEmployee() {
            when(repository.countUpcomingDaysByUser("user@test.com", LocalDate.now())).thenReturn(3L);

            long remaining = bookingService.getRemainingDays("user@test.com", UserRole.EMPLOYEE);

            assertThat(remaining).isEqualTo(2);
        }

        @Test
        @DisplayName("returns 0 when quota is fully used")
        void zeroWhenQuotaFull() {
            when(repository.countUpcomingDaysByUser("user@test.com", LocalDate.now())).thenReturn(5L);

            long remaining = bookingService.getRemainingDays("user@test.com", UserRole.EMPLOYEE);

            assertThat(remaining).isZero();
        }

        @Test
        @DisplayName("never returns negative")
        void neverNegative() {
            when(repository.countUpcomingDaysByUser("user@test.com", LocalDate.now())).thenReturn(10L);

            long remaining = bookingService.getRemainingDays("user@test.com", UserRole.EMPLOYEE);

            assertThat(remaining).isZero();
        }

        @Test
        @DisplayName("manager has 30-day quota")
        void managerHas30DayQuota() {
            when(repository.countUpcomingDaysByUser("mgr@test.com", LocalDate.now())).thenReturn(0L);

            long remaining = bookingService.getRemainingDays("mgr@test.com", UserRole.MANAGER);

            assertThat(remaining).isEqualTo(30);
        }
    }

    @Nested
    @DisplayName("cancelBooking")
    class CancelBooking {

        @Test
        @DisplayName("owner can cancel their own booking")
        void ownerCanCancel() {
            Booking booking = new Booking(1L, "A01", "user@test.com", UserRole.EMPLOYEE,
                    LocalDate.now(), LocalDate.now(), false);
            when(repository.findById(1L)).thenReturn(Optional.of(booking));

            bookingService.cancelBooking(1L, "user@test.com", UserRole.EMPLOYEE);

            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("secretary can cancel any booking")
        void secretaryCanCancelAny() {
            Booking booking = new Booking(1L, "A01", "user@test.com", UserRole.EMPLOYEE,
                    LocalDate.now(), LocalDate.now(), false);
            when(repository.findById(1L)).thenReturn(Optional.of(booking));

            bookingService.cancelBooking(1L, "secretary@test.com", UserRole.SECRETARY);

            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("non-owner non-secretary cannot cancel")
        void nonOwnerNonSecretaryCannotCancel() {
            Booking booking = new Booking(1L, "A01", "user@test.com", UserRole.EMPLOYEE,
                    LocalDate.now(), LocalDate.now(), false);
            when(repository.findById(1L)).thenReturn(Optional.of(booking));

            assertThatThrownBy(() -> bookingService.cancelBooking(1L, "other@test.com", UserRole.EMPLOYEE))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("throws when booking not found")
        void throwsWhenNotFound() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.cancelBooking(99L, "user@test.com", UserRole.EMPLOYEE))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("getUserBookings")
    class GetUserBookings {

        @Test
        @DisplayName("returns all bookings for a user")
        void returnsUserBookings() {
            List<Booking> bookings = List.of(
                    new Booking(1L, "A01", "user@test.com", UserRole.EMPLOYEE,
                            LocalDate.now(), LocalDate.now(), false),
                    new Booking(2L, "B02", "user@test.com", UserRole.EMPLOYEE,
                            LocalDate.now().plusDays(1), LocalDate.now().plusDays(1), false)
            );
            when(repository.findAllByUserEmail("user@test.com")).thenReturn(bookings);

            List<Booking> result = bookingService.getUserBookings("user@test.com");

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("checkIn")
    class CheckIn {

        @Test
        @DisplayName("successfully checks in a valid booking")
        void successfulCheckIn() {
            LocalDate today = LocalDate.now();
            Booking booking = new Booking(1L, "A01", "user@test.com", UserRole.EMPLOYEE,
                    today, today, false);
            when(repository.findByEmailAndSpotIdForDate("user@test.com", "A01", today))
                    .thenReturn(Optional.of(booking));

            Booking result = bookingService.checkIn("A01", "user@test.com");

            assertThat(result.checkedIn()).isTrue();
            verify(repository).checkIn(1L);
        }

        @Test
        @DisplayName("throws when no booking found for check-in")
        void throwsWhenNoBookingFound() {
            when(repository.findByEmailAndSpotIdForDate(anyString(), anyString(), any()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.checkIn("A01", "user@test.com"))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("throws when already checked in")
        void throwsWhenAlreadyCheckedIn() {
            LocalDate today = LocalDate.now();
            Booking booking = new Booking(1L, "A01", "user@test.com", UserRole.EMPLOYEE,
                    today, today, true);
            when(repository.findByEmailAndSpotIdForDate("user@test.com", "A01", today))
                    .thenReturn(Optional.of(booking));

            assertThatThrownBy(() -> bookingService.checkIn("A01", "user@test.com"))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
