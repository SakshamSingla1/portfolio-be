package com.portfolio.repositories;

import com.portfolio.entities.PasswordResetToken;
import com.portfolio.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordResetToken,Integer> {
    PasswordResetToken findByToken(String token);
    void deleteByProfile(Profile profile);
}
