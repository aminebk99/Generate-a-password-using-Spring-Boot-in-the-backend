package com.microservicesapp.passwordgeneration.Controller;

import com.microservicesapp.passwordgeneration.Repository.UserRepository;
import com.microservicesapp.passwordgeneration.Service.PasswordResetTokenService;

import com.microservicesapp.passwordgeneration.entity.PasswordResetToken;
import com.microservicesapp.passwordgeneration.entity.User;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class PasswordResetTokenController {
    private final PasswordResetTokenService passwordResetTokenService;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @PostMapping("/reset-password-request")
    public ResponseEntity<String> resetPassword(@RequestParam String email) {

        try {
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User with provided email does not exist.");
            }
            String token  = passwordResetTokenService.createPasswordResetToken(user);
            String subject = "Password Reset Request";
            String resetPasswordLink = "http://localhost:5173/resetpassword?token=" + token;
            StringBuilder htmlMessage = new StringBuilder();
            htmlMessage.append("<h1>Password Reset</h1>")
                    .append("<p>To reset your password, click the button below:</p>")
                    .append("<a href=\"")
                    .append(resetPasswordLink)
                    .append("\" style=\"background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;\">Reset Password</a>");

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(htmlMessage.toString(), true);
            mailSender.send(message);
            
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Sent password reset link to " + email);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> newPassword(@RequestParam String token, @RequestParam String password) {
        try {
            passwordResetTokenService.resetPassword(token, password);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Your password has been reset.");
        } catch (TokenExpiredException e) { // Use a specific exception for expired tokens
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The token has expired.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while resetting your password.");
        }
    }

    @GetMapping("/tokens")
    public ResponseEntity<List<PasswordResetToken>> getTokens() {
        return ResponseEntity.status(HttpStatus.OK).body(passwordResetTokenService.getAllTokens());
    }
    @DeleteMapping("/tokens/delete-all")
    public ResponseEntity<?> deleteAllTokens() {
        passwordResetTokenService.deleteAllTokens();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("All tokens deleted.");

    }
}
