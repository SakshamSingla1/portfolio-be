package com.portfolio.repositories;

import com.portfolio.entities.PasswordResetToken;
import com.portfolio.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetRepository extends JpaRepository<PasswordResetToken,Integer> {
    PasswordResetToken findByToken(String token);
    void deleteByProfile(Profile profile);
}
