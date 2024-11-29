package com.example.exception;

public class UserNotFoundException extends RuntimeException {

    // Constructor accepting a custom message
    public UserNotFoundException(String message) {
        super(message);
    }

    // Optional: you can add additional constructors, such as one that accepts a cause
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
