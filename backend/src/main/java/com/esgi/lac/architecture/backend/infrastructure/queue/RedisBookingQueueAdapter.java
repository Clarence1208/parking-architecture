package com.esgi.lac.architecture.backend.infrastructure.queue;

import com.esgi.lac.architecture.backend.application.dto.BookingConfirmationMessage;
import com.esgi.lac.architecture.backend.application.repository.BookingQueuePort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisBookingQueueAdapter implements BookingQueuePort {
    private static final String QUEUE_KEY = "booking:queue";
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisBookingQueueAdapter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void publish(BookingConfirmationMessage message) {
        System.out.println(QUEUE_KEY);
        redisTemplate.opsForList().rightPush(QUEUE_KEY, message);
    }
}
