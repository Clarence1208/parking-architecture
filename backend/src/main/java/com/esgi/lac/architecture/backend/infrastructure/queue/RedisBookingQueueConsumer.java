package com.esgi.lac.architecture.backend.infrastructure.queue;

import com.esgi.lac.architecture.backend.application.dto.BookingConfirmationMessage;
import com.esgi.lac.architecture.backend.application.service.MailService;
import com.esgi.lac.architecture.backend.application.usecase.MailUseCase;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Component
public class RedisBookingQueueConsumer {

    private static final String QUEUE_KEY = "booking:queue";
    private final MailUseCase mailService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

        public RedisBookingQueueConsumer(RedisTemplate<String, Object> redisTemplate, MailService mailService, ObjectMapper objectMapper) {
            this.redisTemplate = redisTemplate;
            this.mailService = mailService;
            this.objectMapper = objectMapper;
        }

    @Scheduled(fixedDelay = 500)
    public void consume() {
        Object raw = redisTemplate.opsForList()
                .leftPop(QUEUE_KEY, Duration.ofSeconds(2));
        if (raw != null) {
            BookingConfirmationMessage message = objectMapper.convertValue(raw, BookingConfirmationMessage.class);
            mailService.sendConfirmation(message);
        }
    }
}
