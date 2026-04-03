package com.esgi.lac.architecture.backend.infrastructure.web.mapper;

import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.BookingSpotStatus;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.BookingRequestDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.BookingResponseDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.CheckInResponseDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.UserBookingResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookingControllerMapperTest {

    private final BookingControllerMapper mapper = new BookingControllerMapper();

    @Nested
    @DisplayName("toBooking")
    class ToBooking {

        @Test
        @DisplayName("maps request DTO to domain booking")
        void mapsRequestToDomain() {
            BookingRequestDTO dto = new BookingRequestDTO("A01", "2026-05-01", "2026-05-03");

            Booking booking = mapper.toBooking(dto, "user@test.com", UserRole.EMPLOYEE);

            assertThat(booking.id()).isNull();
            assertThat(booking.spotId()).isEqualTo("A01");
            assertThat(booking.email()).isEqualTo("user@test.com");
            assertThat(booking.role()).isEqualTo(UserRole.EMPLOYEE);
            assertThat(booking.startDate()).isEqualTo(LocalDate.of(2026, 5, 1));
            assertThat(booking.endDate()).isEqualTo(LocalDate.of(2026, 5, 3));
            assertThat(booking.checkedIn()).isFalse();
        }

        @Test
        @DisplayName("parses dates from string format")
        void parsesDates() {
            BookingRequestDTO dto = new BookingRequestDTO("B02", "2026-12-25", "2026-12-31");

            Booking booking = mapper.toBooking(dto, "mgr@test.com", UserRole.MANAGER);

            assertThat(booking.startDate()).isEqualTo(LocalDate.of(2026, 12, 25));
            assertThat(booking.endDate()).isEqualTo(LocalDate.of(2026, 12, 31));
        }
    }

    @Nested
    @DisplayName("toBookingResponse")
    class ToBookingResponse {

        @Test
        @DisplayName("maps spot status to response DTO")
        void mapsSpotStatus() {
            LocalDate date = LocalDate.of(2026, 5, 1);
            BookingSpotStatus spot = new BookingSpotStatus(1L, "A01", true, "user@test.com", date, false);

            BookingResponseDTO response = mapper.toBookingResponse(spot);

            assertThat(response.bookingId()).isEqualTo(1L);
            assertThat(response.spotId()).isEqualTo("A01");
            assertThat(response.occupied()).isTrue();
            assertThat(response.reservedBy()).isEqualTo("user@test.com");
            assertThat(response.date()).isEqualTo(date);
            assertThat(response.checkedIn()).isFalse();
        }
    }

    @Nested
    @DisplayName("toBookingResponseList")
    class ToBookingResponseList {

        @Test
        @DisplayName("maps list of spot statuses")
        void mapsList() {
            LocalDate date = LocalDate.of(2026, 5, 1);
            List<BookingSpotStatus> spots = List.of(
                    new BookingSpotStatus(1L, "A01", true, "user@test.com", date, false),
                    new BookingSpotStatus(null, "A02", false, null, date, false)
            );

            List<BookingResponseDTO> responses = mapper.toBookingResponseList(spots);

            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).spotId()).isEqualTo("A01");
            assertThat(responses.get(1).spotId()).isEqualTo("A02");
            assertThat(responses.get(1).occupied()).isFalse();
        }

        @Test
        @DisplayName("returns empty list for empty input")
        void emptyList() {
            assertThat(mapper.toBookingResponseList(List.of())).isEmpty();
        }
    }

    @Nested
    @DisplayName("toCheckInResponse")
    class ToCheckInResponse {

        @Test
        @DisplayName("maps booking to check-in response")
        void mapsCheckInResponse() {
            Booking booking = new Booking(1L, "A01", "user@test.com", UserRole.EMPLOYEE,
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1), true);

            CheckInResponseDTO response = mapper.toCheckInResponse(booking);

            assertThat(response.bookingId()).isEqualTo(1L);
            assertThat(response.spotId()).isEqualTo("A01");
            assertThat(response.startDate()).isEqualTo(LocalDate.of(2026, 5, 1));
            assertThat(response.endDate()).isEqualTo(LocalDate.of(2026, 5, 1));
            assertThat(response.checkedIn()).isTrue();
        }
    }

    @Nested
    @DisplayName("toUserBookingResponse")
    class ToUserBookingResponse {

        @Test
        @DisplayName("maps booking to user booking response")
        void mapsUserBookingResponse() {
            Booking booking = new Booking(5L, "C03", "user@test.com", UserRole.SECRETARY,
                    LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 5), false);

            UserBookingResponseDTO response = mapper.toUserBookingResponse(booking);

            assertThat(response.id()).isEqualTo(5L);
            assertThat(response.spotId()).isEqualTo("C03");
            assertThat(response.startDate()).isEqualTo(LocalDate.of(2026, 6, 1));
            assertThat(response.endDate()).isEqualTo(LocalDate.of(2026, 6, 5));
            assertThat(response.checkedIn()).isFalse();
        }
    }

    @Nested
    @DisplayName("toUserBookingResponseList")
    class ToUserBookingResponseList {

        @Test
        @DisplayName("maps list of bookings to user booking responses")
        void mapsList() {
            List<Booking> bookings = List.of(
                    new Booking(1L, "A01", "user@test.com", UserRole.EMPLOYEE,
                            LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), false),
                    new Booking(2L, "B02", "user@test.com", UserRole.EMPLOYEE,
                            LocalDate.of(2026, 5, 5), LocalDate.of(2026, 5, 5), true)
            );

            List<UserBookingResponseDTO> responses = mapper.toUserBookingResponseList(bookings);

            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).id()).isEqualTo(1L);
            assertThat(responses.get(1).checkedIn()).isTrue();
        }

        @Test
        @DisplayName("returns empty list for empty input")
        void emptyList() {
            assertThat(mapper.toUserBookingResponseList(List.of())).isEmpty();
        }
    }
}
