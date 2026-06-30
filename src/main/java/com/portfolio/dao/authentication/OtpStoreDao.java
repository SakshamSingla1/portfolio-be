package com.portfolio.dao.authentication;

import com.portfolio.entities.OtpStore;
import com.portfolio.repositories.OtpStoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class OtpStoreDao {

    private final OtpStoreRepository otpStoreRepository;

    public OtpStoreDao(OtpStoreRepository otpStoreRepository) {
        this.otpStoreRepository = otpStoreRepository;
    }

    public OtpStore save(OtpStore otpStore) {
        return otpStoreRepository.save(otpStore);
    }

    public Optional<OtpStore> findById(Long id) {
        return otpStoreRepository.findById(id);
    }

    public Optional<OtpStore> findByProfileId(Long profileId) {
        return otpStoreRepository.findByProfileId(profileId);
    }

    public void deleteByProfileId(Long profileId) {
        otpStoreRepository.deleteByProfileId(profileId);
    }

    public void deleteById(Long id) {
        otpStoreRepository.deleteById(id);
    }
}
