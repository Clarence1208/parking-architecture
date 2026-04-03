package com.esgi.lac.architecture.backend.domain.exception;

public class AlreadyCheckedInException extends DomainException {

    public AlreadyCheckedInException(String message) {
        super(message);
    }
}
