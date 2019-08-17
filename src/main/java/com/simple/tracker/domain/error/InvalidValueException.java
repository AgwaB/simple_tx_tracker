package com.simple.tracker.domain.error;

public class InvalidValueException extends RuntimeException {
    public InvalidValueException(String errorMessage) {
        super(errorMessage);
    }
}
