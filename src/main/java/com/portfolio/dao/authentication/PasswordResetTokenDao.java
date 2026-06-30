package com.portfolio.dao.authentication;

import com.portfolio.entities.PasswordResetToken;
import com.portfolio.repositories.PasswordResetTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class PasswordResetTokenDao {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetTokenDao(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public PasswordResetToken save(PasswordResetToken token) {
        return passwordResetTokenRepository.save(token);
    }

    public Optional<PasswordResetToken> findById(Long id) {
        return passwordResetTokenRepository.findById(id);
    }

    public Optional<PasswordResetToken> findByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    public void deleteByProfileId(Long profileId) {
        passwordResetTokenRepository.deleteByProfileId(profileId);
    }

    public void deleteById(Long id) {
        passwordResetTokenRepository.deleteById(id);
    }

    public void delete(PasswordResetToken token) {
        passwordResetTokenRepository.delete(token);
    }
}
