package com.microservicesapp.passwordgeneration.Controller;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}

