package com.esgi.lac.architecture.backend.infrastructure.persistence.mapper;

import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.infrastructure.persistence.entity.BookingEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingEntityMapperTest {

    private final BookingEntityMapper mapper = new BookingEntityMapper();

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
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("maps all fields from entity to domain")
        void mapsAllFields() {
            BookingEntity entity = makeEntity(1L, "A01", "user@test.com", "MANAGER",
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), true);

            Booking booking = mapper.toDomain(entity);

            assertThat(booking.id()).isEqualTo(1L);
            assertThat(booking.spotId()).isEqualTo("A01");
            assertThat(booking.email()).isEqualTo("user@test.com");
            assertThat(booking.role()).isEqualTo(UserRole.MANAGER);
            assertThat(booking.startDate()).isEqualTo(LocalDate.of(2026, 5, 1));
            assertThat(booking.endDate()).isEqualTo(LocalDate.of(2026, 5, 3));
            assertThat(booking.checkedIn()).isTrue();
        }

        @Test
        @DisplayName("defaults to EMPLOYEE when role is null")
        void defaultsToEmployeeWhenNull() {
            BookingEntity entity = makeEntity(1L, "A01", "user@test.com", null,
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1), false);

            Booking booking = mapper.toDomain(entity);

            assertThat(booking.role()).isEqualTo(UserRole.EMPLOYEE);
        }

        @Test
        @DisplayName("defaults to EMPLOYEE when role is blank")
        void defaultsToEmployeeWhenBlank() {
            BookingEntity entity = makeEntity(1L, "A01", "user@test.com", "  ",
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1), false);

            Booking booking = mapper.toDomain(entity);

            assertThat(booking.role()).isEqualTo(UserRole.EMPLOYEE);
        }

        @Test
        @DisplayName("throws on invalid role string")
        void throwsOnInvalidRole() {
            BookingEntity entity = makeEntity(1L, "A01", "user@test.com", "INVALID",
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1), false);

            assertThatThrownBy(() -> mapper.toDomain(entity))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("maps checkedIn=false correctly")
        void mapsCheckedInFalse() {
            BookingEntity entity = makeEntity(2L, "B02", "other@test.com", "SECRETARY",
                    LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 2), false);

            Booking booking = mapper.toDomain(entity);

            assertThat(booking.checkedIn()).isFalse();
            assertThat(booking.role()).isEqualTo(UserRole.SECRETARY);
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("maps all fields from domain to entity")
        void mapsAllFields() {
            Booking booking = new Booking(null, "A01", "user@test.com", UserRole.MANAGER,
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), false);

            BookingEntity entity = mapper.toEntity(booking);

            assertThat(entity.getSpotId()).isEqualTo("A01");
            assertThat(entity.getEmail()).isEqualTo("user@test.com");
            assertThat(entity.getRole()).isEqualTo("MANAGER");
            assertThat(entity.getStartDate()).isEqualTo(LocalDate.of(2026, 5, 1));
            assertThat(entity.getEndDate()).isEqualTo(LocalDate.of(2026, 5, 3));
        }

        @Test
        @DisplayName("does not set id on new entity")
        void doesNotSetId() {
            Booking booking = new Booking(null, "A01", "user@test.com", UserRole.EMPLOYEE,
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1), false);

            BookingEntity entity = mapper.toEntity(booking);

            assertThat(entity.getId()).isNull();
        }

        @Test
        @DisplayName("converts role enum to string")
        void convertsRoleToString() {
            Booking booking = new Booking(null, "A01", "user@test.com", UserRole.SECRETARY,
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1), false);

            BookingEntity entity = mapper.toEntity(booking);

            assertThat(entity.getRole()).isEqualTo("SECRETARY");
        }
    }
}
