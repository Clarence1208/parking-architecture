package com.esgi.lac.architecture.backend.application.service;

import com.esgi.lac.architecture.backend.application.repository.GreetingQueuePort;
import com.esgi.lac.architecture.backend.application.repository.GreetingReadPort;
import com.esgi.lac.architecture.backend.application.usecase.HelloUseCase;
import org.springframework.stereotype.Service;

@Service
public class HelloService implements HelloUseCase {
    private final GreetingReadPort greetingReadPort;
    private final GreetingQueuePort greetingQueuePort;

    public HelloService(GreetingReadPort greetingReadPort, GreetingQueuePort greetingQueuePort) {
        this.greetingReadPort = greetingReadPort;
        this.greetingQueuePort = greetingQueuePort;
    }

    @Override
    public String greet() {
        return "Hello, World!";
    }

    @Override
    public String greetFromDb() {
        return greetingReadPort.readGreeting();
    }

    @Override
    public String greetFromRedis() {
        return greetingQueuePort.enqueueAndReadGreeting();
    }
}
