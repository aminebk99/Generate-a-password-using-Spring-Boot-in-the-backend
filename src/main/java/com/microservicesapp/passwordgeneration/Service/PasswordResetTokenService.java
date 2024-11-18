package com.microservicesapp.passwordgeneration.Service;

import com.microservicesapp.passwordgeneration.Repository.PasswordResetTokenRepsitory;
import com.microservicesapp.passwordgeneration.Repository.UserRepository;
import com.microservicesapp.passwordgeneration.entity.PasswordResetToken;
import com.microservicesapp.passwordgeneration.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {
    private final PasswordResetTokenRepsitory passwordResetTokenRepository; // Corrected spelling
    private final UserRepository userRepository;

    public String createPasswordResetToken(Optional<User> user) {
        if (user.isPresent()) { // Check if user is present
            String token = UUID.randomUUID().toString();
            saveToken(user, token);
            return token;
        }
        throw new IllegalArgumentException("User must be present");
    }

    public PasswordResetToken saveToken(Optional<User> user, String token) {
        if (token != null && !token.isEmpty() && user.isPresent()) { // Check user presence
            User foundUser = user.get();
            PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setToken(token);
            passwordResetToken.setExpiryDate(LocalDateTime.now());
            passwordResetToken.setEmail(foundUser.getEmail());
            return passwordResetTokenRepository.save(passwordResetToken); // Corrected spelling
        } else {
            throw new IllegalArgumentException("Token or user is invalid");
        }
    }

    public List<PasswordResetToken> getAllTokens() {
        return passwordResetTokenRepository.findAll(); // Corrected spelling
    }

    public void deleteAllTokens() {
        passwordResetTokenRepository.deleteAll(); // Corrected spelling
    }

//    public boolean validateToken(String token) {
//        PasswordResetToken token1 = passwordResetTokenRepository.findByToken(token);
//        if (token1 == null) {
//            return false;
//        }
//        return !token1.getExpiryDate().toInstant().isBefore(Instant.now());
//    }
private boolean isTokenExpired(LocalDateTime expiryDate) {
    LocalDateTime currentTime = LocalDateTime.now();
    return currentTime.isAfter(expiryDate.plusMinutes(2));
}

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken tokenEntity = passwordResetTokenRepository.findByToken(token);

        if (tokenEntity != null && !isTokenExpired(tokenEntity.getExpiryDate())) {
            Optional<User> userExist = userRepository.findByEmail(tokenEntity.getEmail());
            if (userExist.isPresent()) {
                User user = userExist.get();
                user.setPassword(newPassword);
                userRepository.save(user);
                passwordResetTokenRepository.delete(tokenEntity);
            } else {
                throw new RuntimeException("User not found for the provided token");
            }
        } else {
            throw new RuntimeException("Token is invalid or expired");
        }
    }

}

