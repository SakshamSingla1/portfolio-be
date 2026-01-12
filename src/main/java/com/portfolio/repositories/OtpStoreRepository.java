package com.portfolio.repositories;

import com.portfolio.entities.OtpStore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpStoreRepository extends MongoRepository<OtpStore, String> {
    void deleteByProfileId(String email);
    Optional<OtpStore> findByProfileId(String email);
}
