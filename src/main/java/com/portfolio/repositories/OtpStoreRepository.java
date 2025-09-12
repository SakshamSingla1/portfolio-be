package com.portfolio.repositories;

import com.portfolio.entities.OtpStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpStoreRepository extends JpaRepository<OtpStore, Integer> {
    Optional<OtpStore> findTopByProfilePhoneOrderByCreatedAtDesc(String mobile);
    void deleteAllByProfilePhone(String mobile);
    void deleteAllByExpiryDateBefore(LocalDateTime now);
}
