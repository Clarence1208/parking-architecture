package com.esgi.lac.architecture.backend.infrastructure.queue;

import com.esgi.lac.architecture.backend.application.dto.BookingConfirmationMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;

public class RedisBookingQueueConsumer {

    private static final String QUEUE_KEY = "booking:queue";

    private final RedisTemplate<String, Object> redisTemplate;
    public RedisBookingQueueConsumer(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
//    private final MailService mailService;

    @Scheduled(fixedDelay = 100)
    public void consume() {
        Object raw = redisTemplate.opsForList()
                .leftPop(QUEUE_KEY, Duration.ofSeconds(5));

        if (raw instanceof BookingConfirmationMessage message) {
//            mailService.sendConfirmation(message);
            System.out.println(message);
        }
    }
}
