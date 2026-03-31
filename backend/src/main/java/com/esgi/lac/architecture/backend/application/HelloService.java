package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.domain.usecase.HelloUseCase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class HelloService implements HelloUseCase {
    private final JdbcTemplate jdbcTemplate;

    public HelloService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String greet() {
        return "Hello, World!";
    }

    @Override
    public String greetFromDb() {
        return jdbcTemplate.queryForObject("SELECT 'Hello from PostgreSQL!'", String.class);
    }
}
