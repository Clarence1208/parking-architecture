package com.esgi.lac.architecture.backend.domain.usecase;

public interface HelloUseCase {
    String greet();
    String greetFromDb();
    String greetFromRedis();
}
