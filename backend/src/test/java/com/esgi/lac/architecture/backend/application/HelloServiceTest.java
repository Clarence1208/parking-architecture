package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.application.repository.GreetingQueuePort;
import com.esgi.lac.architecture.backend.application.repository.GreetingReadPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HelloServiceTest {

    @Mock
    private GreetingReadPort greetingReadPort;

    @Mock
    private GreetingQueuePort greetingQueuePort;

    @InjectMocks
    private HelloService helloService;

    @Test
    void greetReturnsExpectedMessage() {
        assertEquals("Hello, World!", helloService.greet());
    }

    @Test
    void greetFromDbReturnsMessageFromSqlQuery() {
        when(greetingReadPort.readGreeting()).thenReturn("Hello from PostgreSQL!");

        String result = helloService.greetFromDb();

        assertEquals("Hello from PostgreSQL!", result);
        verify(greetingReadPort).readGreeting();
    }

    @Test
    void greetFromRedisPushesAndPopsQueueMessage() {
        when(greetingQueuePort.enqueueAndReadGreeting()).thenReturn("Hello from Redis queue!");

        String result = helloService.greetFromRedis();

        assertEquals("Hello from Redis queue!", result);
        verify(greetingQueuePort).enqueueAndReadGreeting();
    }

    @Test
    void greetFromRedisReturnsFallbackWhenQueueIsEmpty() {
        when(greetingQueuePort.enqueueAndReadGreeting()).thenReturn("Redis queue is empty.");

        String result = helloService.greetFromRedis();

        assertEquals("Redis queue is empty.", result);
    }
}
