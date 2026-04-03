package com.esgi.lac.architecture.backend.infrastructure.web.mapper;

import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.BookingSpotStatus;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.BookingRequestDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.BookingResponseDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.CheckInResponseDTO;
import com.esgi.lac.architecture.backend.infrastructure.web.dto.UserBookingResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class BookingControllerMapper {

    public Booking toBooking(BookingRequestDTO dto, String email, UserRole role) {
        return new Booking(
                null,
                dto.spotId(),
                email,
                role,
                LocalDate.parse(dto.startDate()),
                LocalDate.parse(dto.endDate()),
                false
        );
    }

    public BookingResponseDTO toBookingResponse(BookingSpotStatus spot) {
        return new BookingResponseDTO(
                spot.bookingId(),
                spot.spotId(),
                spot.occupied(),
                spot.reservedBy(),
                spot.date(),
                spot.checkedIn()
        );
    }

    public List<BookingResponseDTO> toBookingResponseList(List<BookingSpotStatus> spots) {
        return spots.stream().map(this::toBookingResponse).toList();
    }

    public CheckInResponseDTO toCheckInResponse(Booking booking) {
        return new CheckInResponseDTO(
                booking.id(),
                booking.spotId(),
                booking.startDate(),
                booking.endDate(),
                booking.checkedIn()
        );
    }

    public UserBookingResponseDTO toUserBookingResponse(Booking booking) {
        return new UserBookingResponseDTO(
                booking.id(),
                booking.spotId(),
                booking.startDate(),
                booking.endDate(),
                booking.checkedIn()
        );
    }

    public List<UserBookingResponseDTO> toUserBookingResponseList(List<Booking> bookings) {
        return bookings.stream().map(this::toUserBookingResponse).toList();
    }
}
