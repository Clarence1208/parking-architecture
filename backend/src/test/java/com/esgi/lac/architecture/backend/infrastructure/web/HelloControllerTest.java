package com.esgi.lac.architecture.backend.infrastructure.web;

import com.esgi.lac.architecture.backend.application.usecase.HelloUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HelloControllerTest {

    @Mock
    private HelloUseCase helloUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        HelloController helloController = new HelloController(helloUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(helloController).build();
    }

    @Test
    void helloEndpointReturnsGreeting() throws Exception {
        when(helloUseCase.greet()).thenReturn("Hello, World!");

        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, World!"));
    }

    @Test
    void helloDbEndpointReturnsDbGreeting() throws Exception {
        when(helloUseCase.greetFromDb()).thenReturn("Hello from PostgreSQL!");

        mockMvc.perform(get("/api/hello/db"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from PostgreSQL!"));
    }

    @Test
    void helloRedisEndpointReturnsRedisGreeting() throws Exception {
        when(helloUseCase.greetFromRedis()).thenReturn("Hello from Redis queue!");

        mockMvc.perform(get("/api/hello/redis"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from Redis queue!"));
    }
}
