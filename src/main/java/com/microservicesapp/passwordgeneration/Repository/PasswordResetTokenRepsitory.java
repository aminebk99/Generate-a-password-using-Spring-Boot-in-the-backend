package com.microservicesapp.passwordgeneration.Repository;

import com.microservicesapp.passwordgeneration.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepsitory extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
}
