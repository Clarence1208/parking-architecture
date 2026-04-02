package com.esgi.lac.architecture.backend.infrastructure.queue;

import com.esgi.lac.architecture.backend.application.dto.BookingConfirmationMessage;
import com.esgi.lac.architecture.backend.domain.model.Booking;
import com.esgi.lac.architecture.backend.domain.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisBookingQueueAdapterTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ListOperations<String, Object> listOps;

    @InjectMocks
    private RedisBookingQueueAdapter adapter;

    @Test
    @DisplayName("publishes message to the booking queue via RPUSH")
    void publishesToQueue() {
        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.rightPush(eq("booking:queue"), org.mockito.ArgumentMatchers.any()))
                .thenReturn(1L);

        BookingConfirmationMessage message = BookingConfirmationMessage.fromBooking(
                new Booking(1L, "A01", "user@test.com", UserRole.EMPLOYEE,
                        LocalDate.now(), LocalDate.now(), false)
        );

        adapter.publish(message);

        verify(listOps).rightPush(eq("booking:queue"), eq(message));
    }
}
