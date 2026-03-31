package com.esgi.lac.architecture.backend.application.usecase;

public interface HelloUseCase {
    String greet();
    String greetFromDb();
    String greetFromRedis();
}
