package com.esgi.lac.architecture.backend.infrastructure.persistence;

import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.infrastructure.persistence.entity.BookingEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingRepositoryAdapterTest {

    @Mock
    private JpaBookingRepository jpaBookingRepository;

    @InjectMocks
    private BookingRepositoryAdapter adapter;

    private BookingEntity makeEntity(Long id, String spotId, String email, String role,
                                     LocalDate start, LocalDate end, boolean checkedIn) {
        BookingEntity e = new BookingEntity();
        e.setId(id);
        e.setSpotId(spotId);
        e.setEmail(email);
        e.setRole(role);
        e.setStartDate(start);
        e.setEndDate(end);
        e.setCheckedIn(checkedIn);
        return e;
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("maps domain Booking to entity correctly")
        void mapsDomainToEntity() {
            Booking booking = new Booking(null, "A01", "user@test.com", UserRole.MANAGER,
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), false);

            adapter.save(booking);

            ArgumentCaptor<BookingEntity> captor = ArgumentCaptor.forClass(BookingEntity.class);
            verify(jpaBookingRepository).save(captor.capture());
            BookingEntity saved = captor.getValue();

            assertThat(saved.getSpotId()).isEqualTo("A01");
            assertThat(saved.getEmail()).isEqualTo("user@test.com");
            assertThat(saved.getRole()).isEqualTo("MANAGER");
            assertThat(saved.getStartDate()).isEqualTo(LocalDate.of(2026, 5, 1));
            assertThat(saved.getEndDate()).isEqualTo(LocalDate.of(2026, 5, 3));
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("maps entity to domain when found")
        void mapsEntityToDomain() {
            BookingEntity entity = makeEntity(1L, "A01", "user@test.com", "EMPLOYEE",
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1), true);
            when(jpaBookingRepository.findById(1L)).thenReturn(Optional.of(entity));

            Optional<Booking> result = adapter.findById(1L);

            assertThat(result).isPresent();
            Booking booking = result.get();
            assertThat(booking.id()).isEqualTo(1L);
            assertThat(booking.spotId()).isEqualTo("A01");
            assertThat(booking.role()).isEqualTo(UserRole.EMPLOYEE);
            assertThat(booking.checkedIn()).isTrue();
        }

        @Test
        @DisplayName("returns empty when not found")
        void returnsEmptyWhenNotFound() {
            when(jpaBookingRepository.findById(99L)).thenReturn(Optional.empty());

            assertThat(adapter.findById(99L)).isEmpty();
        }

        @Test
        @DisplayName("defaults to EMPLOYEE when role is null or blank")
        void defaultsToEmployeeForNullRole() {
            BookingEntity entity = makeEntity(1L, "A01", "user@test.com", null,
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1), false);
            when(jpaBookingRepository.findById(1L)).thenReturn(Optional.of(entity));

            Booking booking = adapter.findById(1L).orElseThrow();
            assertThat(booking.role()).isEqualTo(UserRole.EMPLOYEE);
        }
    }

    @Nested
    @DisplayName("countUpcomingDaysByUser")
    class CountUpcomingDays {

        @Test
        @DisplayName("counts days correctly for a single future booking")
        void countsSingleBooking() {
            LocalDate today = LocalDate.of(2026, 5, 1);
            BookingEntity entity = makeEntity(1L, "A01", "user@test.com", "EMPLOYEE",
                    LocalDate.of(2026, 5, 2), LocalDate.of(2026, 5, 4), false);
            when(jpaBookingRepository.findUpcomingByUser("user@test.com", today))
                    .thenReturn(List.of(entity));

            long count = adapter.countUpcomingDaysByUser("user@test.com", today);

            assertThat(count).isEqualTo(3); // May 2, 3, 4
        }

        @Test
        @DisplayName("only counts days from fromDate onwards for partially past bookings")
        void countsOnlyFromDate() {
            LocalDate today = LocalDate.of(2026, 5, 3);
            BookingEntity entity = makeEntity(1L, "A01", "user@test.com", "EMPLOYEE",
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 5), false);
            when(jpaBookingRepository.findUpcomingByUser("user@test.com", today))
                    .thenReturn(List.of(entity));

            long count = adapter.countUpcomingDaysByUser("user@test.com", today);

            assertThat(count).isEqualTo(3); // May 3, 4, 5
        }

        @Test
        @DisplayName("sums days across multiple bookings")
        void sumsMultipleBookings() {
            LocalDate today = LocalDate.of(2026, 5, 1);
            BookingEntity b1 = makeEntity(1L, "A01", "user@test.com", "EMPLOYEE",
                    LocalDate.of(2026, 5, 2), LocalDate.of(2026, 5, 3), false);
            BookingEntity b2 = makeEntity(2L, "B02", "user@test.com", "EMPLOYEE",
                    LocalDate.of(2026, 5, 5), LocalDate.of(2026, 5, 5), false);
            when(jpaBookingRepository.findUpcomingByUser("user@test.com", today))
                    .thenReturn(List.of(b1, b2));

            long count = adapter.countUpcomingDaysByUser("user@test.com", today);

            assertThat(count).isEqualTo(3); // 2 + 1
        }

        @Test
        @DisplayName("returns 0 when no upcoming bookings")
        void zeroWhenNone() {
            when(jpaBookingRepository.findUpcomingByUser("user@test.com", LocalDate.now()))
                    .thenReturn(List.of());

            long count = adapter.countUpcomingDaysByUser("user@test.com", LocalDate.now());

            assertThat(count).isZero();
        }
    }

    @Nested
    @DisplayName("findAllOverlappingDate")
    class FindAllOverlappingDate {

        @Test
        @DisplayName("maps entities to domain objects")
        void mapsToDomain() {
            LocalDate date = LocalDate.of(2026, 5, 1);
            BookingEntity entity = makeEntity(1L, "A01", "user@test.com", "MANAGER",
                    date, date, false);
            when(jpaBookingRepository.findAllOverlappingDate(date)).thenReturn(List.of(entity));

            List<Booking> result = adapter.findAllOverlappingDate(date);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().role()).isEqualTo(UserRole.MANAGER);
        }
    }

    @Nested
    @DisplayName("delegating methods")
    class DelegatingMethods {

        @Test
        @DisplayName("deleteById delegates to JPA")
        void deleteByIdDelegates() {
            adapter.deleteById(1L);
            verify(jpaBookingRepository).deleteById(1L);
        }

        @Test
        @DisplayName("checkIn delegates to JPA")
        void checkInDelegates() {
            adapter.checkIn(1L);
            verify(jpaBookingRepository).checkIn(1L);
        }

        @Test
        @DisplayName("existsOverlappingSpotBooking delegates to JPA")
        void existsOverlappingSpotDelegates() {
            LocalDate start = LocalDate.now();
            LocalDate end = LocalDate.now();
            adapter.existsOverlappingSpotBooking("A01", start, end);
            verify(jpaBookingRepository).existsOverlappingSpotBooking("A01", start, end);
        }

        @Test
        @DisplayName("existsOverlappingUserBooking delegates to JPA")
        void existsOverlappingUserDelegates() {
            LocalDate start = LocalDate.now();
            LocalDate end = LocalDate.now();
            adapter.existsOverlappingUserBooking("user@test.com", start, end);
            verify(jpaBookingRepository).existsOverlappingUserBooking("user@test.com", start, end);
        }

        @Test
        @DisplayName("updateStartDate delegates to JPA")
        void updateStartDateDelegates() {
            adapter.updateStartDate(1L, LocalDate.of(2026, 5, 2));
            verify(jpaBookingRepository).updateStartDate(1L, LocalDate.of(2026, 5, 2));
        }

        @Test
        @DisplayName("updateEndDate delegates to JPA")
        void updateEndDateDelegates() {
            adapter.updateEndDate(1L, LocalDate.of(2026, 5, 10));
            verify(jpaBookingRepository).updateEndDate(1L, LocalDate.of(2026, 5, 10));
        }

        @Test
        @DisplayName("resetCheckedInForMultiDayBookings delegates to JPA")
        void resetCheckedInDelegates() {
            adapter.resetCheckedInForMultiDayBookings(LocalDate.now());
            verify(jpaBookingRepository).resetCheckedInForMultiDayBookings(LocalDate.now());
        }
    }
}
