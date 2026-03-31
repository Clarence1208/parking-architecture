package com.esgi.lac.architecture.backend.infrastructure.persistence;

import com.esgi.lac.architecture.backend.application.repository.GreetingReadPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcGreetingAdapter implements GreetingReadPort {
    private final JdbcTemplate jdbcTemplate;

    public JdbcGreetingAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String readGreeting() {
        return jdbcTemplate.queryForObject("SELECT 'Hello from PostgreSQL!'", String.class);
    }
}
