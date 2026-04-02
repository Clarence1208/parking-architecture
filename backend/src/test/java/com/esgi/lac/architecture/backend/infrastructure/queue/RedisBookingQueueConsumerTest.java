package com.esgi.lac.architecture.backend.infrastructure.queue;

import com.esgi.lac.architecture.backend.application.dto.BookingConfirmationMessage;
import com.esgi.lac.architecture.backend.application.service.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisBookingQueueConsumerTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private MailService mailService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ListOperations<String, Object> listOps;

    @Test
    @DisplayName("processes a message from the queue and sends confirmation")
    void processesMessage() {
        when(redisTemplate.opsForList()).thenReturn(listOps);

        Map<String, Object> rawMessage = new LinkedHashMap<>();
        rawMessage.put("bookingId", 1);
        rawMessage.put("recipientEmail", "user@test.com");
        rawMessage.put("parkingSpotId", "A01");
        rawMessage.put("startDate", "2026-05-01");
        rawMessage.put("endDate", "2026-05-01");

        when(listOps.leftPop("booking:queue", Duration.ofSeconds(2))).thenReturn(rawMessage);

        BookingConfirmationMessage expected = new BookingConfirmationMessage();
        when(objectMapper.convertValue(rawMessage, BookingConfirmationMessage.class))
                .thenReturn(expected);

        RedisBookingQueueConsumer consumer = new RedisBookingQueueConsumer(
                redisTemplate, mailService, objectMapper);

        consumer.consume();

        verify(mailService).sendConfirmation(expected);
    }

    @Test
    @DisplayName("does nothing when queue is empty")
    void doesNothingWhenEmpty() {
        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.leftPop("booking:queue", Duration.ofSeconds(2))).thenReturn(null);

        RedisBookingQueueConsumer consumer = new RedisBookingQueueConsumer(
                redisTemplate, mailService, objectMapper);

        consumer.consume();

        verify(mailService, never()).sendConfirmation(any());
    }
}
