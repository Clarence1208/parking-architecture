package com.esgi.lac.architecture.backend.application.exception;

public class DuplicateEmailException extends ApplicationException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}
