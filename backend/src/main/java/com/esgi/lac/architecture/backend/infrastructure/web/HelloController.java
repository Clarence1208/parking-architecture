package com.esgi.lac.architecture.backend.infrastructure.web;

import com.esgi.lac.architecture.backend.domain.usecase.HelloUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Hello", description = "Hello world operations")
@RestController
@RequestMapping("/api")
public class HelloController {

    private final HelloUseCase helloUseCase;

    public HelloController(HelloUseCase helloUseCase) {
        this.helloUseCase = helloUseCase;
    }

    @Operation(summary = "Say hello", description = "Returns a Hello World greeting")
    @ApiResponse(responseCode = "200", description = "Greeting returned successfully")
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok(helloUseCase.greet());
    }

    @Operation(summary = "Say hello from database", description = "Returns a greeting fetched from PostgreSQL")
    @ApiResponse(responseCode = "200", description = "Database greeting returned successfully")
    @GetMapping("/hello/db")
    public ResponseEntity<String> helloDb() {
        return ResponseEntity.ok(helloUseCase.greetFromDb());
    }

    @Operation(summary = "Say hello from redis queue", description = "Pushes then pops a message from Redis queue")
    @ApiResponse(responseCode = "200", description = "Redis queue message returned successfully")
    @GetMapping("/hello/redis")
    public ResponseEntity<String> helloRedis() {
        return ResponseEntity.ok(helloUseCase.greetFromRedis());
    }
}
