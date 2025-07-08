package com.portfolio.services;

import com.portfolio.dtos.*;
import com.portfolio.entities.PasswordResetToken;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.repositories.PasswordResetRepository;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AdminService implements UserDetailsService {

    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetRepository passwordResetRepository;
    private final MailService mailService;

    public AdminService(ProfileRepository profileRepository,
                        PasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil,
                        PasswordResetRepository passwordResetRepository,
                        MailService mailService) {
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.passwordResetRepository = passwordResetRepository;
        this.mailService = mailService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Profile admin = profileRepository.findByEmail(email);
        if (admin == null) {
            throw new UsernameNotFoundException("Admin not found with email: " + email);
        }

        return new org.springframework.security.core.userdetails.User(
                admin.getEmail(),
                admin.getPassword(),
                List.of(new SimpleGrantedAuthority("ADMIN"))
        );
    }

    public ResponseEntity<ResponseModel<AdminResponse>> register(AdminRegisterRequest request) throws GenericException {
        if (profileRepository.count() > 0) {
            return ApiResponse.failureResponse(null,"Admin already present");
        }

        Profile admin = Profile.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role("ADMIN")
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Profile saved = profileRepository.save(admin);
        String token = jwtUtil.generateAccessToken(admin);

        AdminResponse response = AdminResponse.builder()
                .id(saved.getId())
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .role(saved.getRole())
                .token(token)
                .build();

        return ApiResponse.successResponse(response, "Admin registered successfully");
    }

    public ResponseEntity<ResponseModel<AdminResponse>> login(AdminLoginRequest request) throws GenericException {
        Profile admin = profileRepository.findByEmail(request.getEmail());
        if (admin == null || !passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            return ApiResponse.failureResponse(null,"Invalid email or password");
        }
        String token = jwtUtil.generateAccessToken(admin);
        AdminResponse response = AdminResponse.builder()
                .id(admin.getId())
                .fullName(admin.getFullName())
                .email(admin.getEmail())
                .role(admin.getRole())
                .token(token)
                .build();
        return ApiResponse.successResponse(response, "Admin logged in successfully");
    }

    @Transactional
    public ResponseEntity<ResponseModel<String>> forgotPassword(ForgotPasswordRequest request) throws GenericException {
        Profile admin = profileRepository.findByEmail(request.getEmail());
        System.out.println(admin);
        if (admin == null) {
            return ApiResponse.failureResponse(null,"Admin not found");
        }
        passwordResetRepository.deleteByProfile(admin);
        String token = jwtUtil.generateAccessToken(admin);
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .profile(admin)
                .expiryDate(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 minutes
                .build();
        passwordResetRepository.save(resetToken);
        mailService.sendPasswordResetEmail(admin.getEmail(), admin.getFullName(), token);
        return ApiResponse.successResponse("Done","Password reset link sent successfully");
    }

    public ResponseEntity<ResponseModel<String>> resetPassword(ResetPasswordRequest resetPasswordRequest) throws GenericException {
        PasswordResetToken resetToken = passwordResetRepository.findByToken(resetPasswordRequest.getToken());
        if (resetToken == null || resetToken.getExpiryDate().before(new Date())) {
            return ApiResponse.failureResponse(null,"Token expired or invalid");
        }
        Profile profile = resetToken.getProfile();
        profile.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        profileRepository.save(profile);
        passwordResetRepository.delete(resetToken);
        return ApiResponse.successResponse("Password reset successfully", "Done");
    }
}
