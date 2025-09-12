package com.portfolio.services;

import com.portfolio.entities.OtpStore;
import com.portfolio.entities.Profile;
import com.portfolio.repositories.OtpStoreRepository;
import com.portfolio.repositories.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpStorageService {

    private final OtpStoreRepository otpStoreRepository;
    private final ProfileRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void saveOtp(String mobileNumber, String plainOtp) {
        Profile user = userRepository.findByPhone(mobileNumber);
        if (user == null){
            throw new IllegalArgumentException("User not found with mobileNumber: " + mobileNumber);
        }

        String hashedOtp = passwordEncoder.encode(plainOtp);

        OtpStore otpStore = OtpStore.builder()
                .profile(user)
                .otp(hashedOtp)
                .createdAt(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .build();

        otpStoreRepository.save(otpStore);
    }

    public boolean validateOtp(String mobileNumber, String plainOtp) {
        Optional<OtpStore> otpStoreOpt = otpStoreRepository.findTopByProfilePhoneOrderByCreatedAtDesc(mobileNumber);

        if (otpStoreOpt.isEmpty()) return false;

        OtpStore otpStore = otpStoreOpt.get();

        if (otpStore.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpStoreRepository.delete(otpStore);
            return false;
        }

        boolean isValid = passwordEncoder.matches(plainOtp, otpStore.getOtp());

        if (isValid) {
            otpStoreRepository.delete(otpStore);
        }

        return isValid;
    }

    public void removeOtp(String mobileNumber) {
        otpStoreRepository.deleteAllByProfilePhone(mobileNumber);
    }

    @Transactional
    public void removeExpiredOtps() {
        otpStoreRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
    }

    @Scheduled(fixedRate = 600000)
    public void autoCleanExpiredOtps() {
        // Call through proxy, not "this"
        self.removeExpiredOtps();
    }

    @Lazy
    @Autowired
    private OtpStorageService self;
}
