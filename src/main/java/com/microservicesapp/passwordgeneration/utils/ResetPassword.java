package com.microservicesapp.passwordgeneration.utils;

import java.util.UUID;

public class ResetPassword  {
    // Generate a new unique token
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    // Validate token (for example purposes, just checks if it's not null or empty)
    public static boolean validateToken(String token) {
        return token != null && !token.isEmpty();
    }
}
