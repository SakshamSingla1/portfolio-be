package com.portfolio.repositories;

import com.portfolio.entities.OtpStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpStoreRepository extends JpaRepository<OtpStore, Long> {

    void deleteByProfileId(Long profileId);

    Optional<OtpStore> findByProfileId(Long profileId);
}
