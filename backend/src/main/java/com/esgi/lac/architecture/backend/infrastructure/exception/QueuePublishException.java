package com.esgi.lac.architecture.backend.infrastructure.exception;

public class QueuePublishException extends InfrastructureException {

    public QueuePublishException(String message) {
        super(message);
    }

    public QueuePublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
