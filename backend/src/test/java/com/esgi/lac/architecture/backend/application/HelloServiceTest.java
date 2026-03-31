package com.esgi.lac.architecture.backend.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HelloServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ListOperations<String, String> listOperations;

    @InjectMocks
    private HelloService helloService;

    @Test
    void greetReturnsExpectedMessage() {
        assertEquals("Hello, World!", helloService.greet());
    }

    @Test
    void greetFromDbReturnsMessageFromSqlQuery() {
        when(jdbcTemplate.queryForObject("SELECT 'Hello from PostgreSQL!'", String.class))
            .thenReturn("Hello from PostgreSQL!");

        String result = helloService.greetFromDb();

        assertEquals("Hello from PostgreSQL!", result);
        verify(jdbcTemplate).queryForObject("SELECT 'Hello from PostgreSQL!'", String.class);
    }

    @Test
    void greetFromRedisPushesAndPopsQueueMessage() {
        when(stringRedisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.rightPop(eq("hello-queue"))).thenReturn("Hello from Redis queue!");

        String result = helloService.greetFromRedis();

        assertEquals("Hello from Redis queue!", result);
        verify(listOperations).leftPush("hello-queue", "Hello from Redis queue!");
        verify(listOperations).rightPop("hello-queue");
    }

    @Test
    void greetFromRedisReturnsFallbackWhenQueueIsEmpty() {
        when(stringRedisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.rightPop(eq("hello-queue"))).thenReturn(null);

        String result = helloService.greetFromRedis();

        assertEquals("Redis queue is empty.", result);
    }
}
