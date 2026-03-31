package com.esgi.lac.architecture.backend.infrastructure.persistence;

import com.esgi.lac.architecture.backend.application.repository.GreetingQueuePort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisGreetingQueueAdapter implements GreetingQueuePort {
    private final StringRedisTemplate stringRedisTemplate;

    public RedisGreetingQueueAdapter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public String enqueueAndReadGreeting() {
        String queueName = "hello-queue";
        String message = "Hello from Redis queue!";
        stringRedisTemplate.opsForList().leftPush(queueName, message);
        String queuedMessage = stringRedisTemplate.opsForList().rightPop(queueName);
        return queuedMessage != null ? queuedMessage : "Redis queue is empty.";
    }
}
