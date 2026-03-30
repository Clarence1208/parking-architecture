package com.esgi.lac.architecture.backend.application;

import com.esgi.lac.architecture.backend.domain.usecase.HelloUseCase;
import org.springframework.stereotype.Service;

@Service
public class HelloService implements HelloUseCase {

    @Override
    public String greet() {
        return "Hello, World!";
    }
}
