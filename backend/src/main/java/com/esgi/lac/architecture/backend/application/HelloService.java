package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.domain.usecase.HelloUseCase;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class HelloService implements HelloUseCase {
    private final JdbcTemplate jdbcTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public HelloService(JdbcTemplate jdbcTemplate, StringRedisTemplate stringRedisTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public String greet() {
        return "Hello, World!";
    }

    @Override
    public String greetFromDb() {
        return jdbcTemplate.queryForObject("SELECT 'Hello from PostgreSQL!'", String.class);
    }

    @Override
    public String greetFromRedis() {
        String queueName = "hello-queue";
        String message = "Hello from Redis queue!";
        stringRedisTemplate.opsForList().leftPush(queueName, message);
        String queuedMessage = stringRedisTemplate.opsForList().rightPop(queueName);
        return queuedMessage != null ? queuedMessage : "Redis queue is empty.";
    }
}
