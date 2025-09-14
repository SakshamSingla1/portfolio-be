package com.portfolio.services;

import com.portfolio.dtos.*;
import com.portfolio.entities.PasswordResetToken;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.PasswordResetRepository;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
public class AdminService {

    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetRepository passwordResetRepository;
    private final MailService mailService;
    private final OtpStorageService otpStorageService;

    private static final long RESET_TOKEN_EXPIRY_MS = 15 * 60 * 1000;

    public AdminService(ProfileRepository profileRepository,
                        PasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil,
                        PasswordResetRepository passwordResetRepository,
                        MailService mailService,
                        OtpStorageService otpStorageService) {
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.passwordResetRepository = passwordResetRepository;
        this.mailService = mailService;
        this.otpStorageService = otpStorageService;
    }

    // 🔹 REGISTER NEW ADMIN
    @Transactional
    public AdminResponse register(AdminRegisterRequest request) throws GenericException {
        validateRegistrationRequest(request);

        Profile admin = Profile.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .fullName(request.getFullName())
                .role("ADMIN")
                .password(passwordEncoder.encode(request.getPassword()))
                .verified(false) // wait until OTP validated
                .build();

        profileRepository.save(admin);

        // Send OTP to email
        sendOtpToUser(admin);

        return AdminResponse.builder()
                .id(admin.getId())
                .email(admin.getEmail())
                .fullName(admin.getFullName())
                .role(admin.getRole())
                .message("Registered successfully. OTP sent for verification.")
                .build();
    }

    // 🔹 VERIFY REGISTRATION OTP
    public void verifyRegistrationOtp(VerifyOtpRequest request) throws GenericException {
        Profile admin = profileRepository.findByPhone(request.getPhone());
        if (admin == null) {
            throw new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "No admin found with this phone");
        }
        if (admin.isVerified()) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "Admin already verified");
        }

        if (!otpStorageService.validateOtp(request.getPhone(), request.getOtp())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Invalid or expired OTP");
        }

        admin.setVerified(true);
        profileRepository.save(admin);
    }

    // 🔹 LOGIN ADMIN
    public AdminResponse login(AdminLoginRequest request) throws GenericException {
        Profile admin = profileRepository.findByEmail(request.getEmail());
        if (admin == null || !passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid email or password");
        }

        if (!admin.isVerified()) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "Admin not verified.");
        }

        String token = jwtUtil.generateAccessToken(admin);
        return buildAdminResponse(admin, token);
    }

    // 🔹 REQUEST CHANGE EMAIL (Send OTP to new email)
    public String requestChangeEmailOtp(int id, ChangeEmailRequest request) throws GenericException {
        Profile admin = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Admin not found"));

        if (profileRepository.findByEmail(request.getEmail()) != null) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_PROFILE, "Email already in use");
        }

        String otp = generateOtp();
        otpStorageService.saveOtp(admin.getPhone(), otp);
        mailService.sendOtpVerificationEmail(request.getEmail(), admin.getFullName(), otp);

        return "OTP sent to new email. Verify to complete email change.";
    }

    // 🔹 VERIFY CHANGE EMAIL OTP
    public String verifyChangeEmailOtp(int id, VerifyOtpRequest request) throws GenericException {
        Profile admin = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Admin not found"));

        if (!otpStorageService.validateOtp(admin.getPhone(), request.getOtp())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Invalid or expired OTP");
        }

        if (request.getNewEmail() == null) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "New email must be provided");
        }

        admin.setEmail(request.getNewEmail());
        profileRepository.save(admin);
        return "Email changed successfully.";
    }

    // 🔹 REQUEST DELETE PROFILE (Send OTP)
    public String requestDeleteProfileOtp(int id) throws GenericException {
        Profile admin = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Admin not found"));
        String otp = generateOtp();
        otpStorageService.saveOtp(admin.getPhone(), otp);
        mailService.sendOtpVerificationEmail(admin.getEmail(), admin.getFullName(), otp);

        return "OTP sent to registered email/phone for profile deletion confirmation.";
    }

    // 🔹 SEND OTP TO USER (for verification, reuse)
    public String sendOtp(String phone) throws GenericException {
        Profile user = profileRepository.findByPhone(phone);
        if (user == null) {
            throw new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found with mobile number: " + phone);
        }

        return sendOtpToUser(user);
    }

    // 🔹 DELETE PROFILE (Password + OTP)
    public void deleteProfile(int id, PasswordRequest request) throws GenericException {
        Profile admin = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Admin not found"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Incorrect password.");
        }

        if (!otpStorageService.validateOtp(admin.getPhone(), request.getOtp())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Invalid or expired OTP");
        }

        profileRepository.delete(admin);
    }

    // 🔹 FORGOT PASSWORD (Send reset token to email)
    public String forgotPassword(ForgotPasswordRequest request) throws GenericException {
        Profile admin = profileRepository.findByEmail(request.getEmail());
        if (admin == null) {
            throw new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "No admin with this email.");
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .profile(admin)
                .expiryDate(new Date(System.currentTimeMillis() + RESET_TOKEN_EXPIRY_MS))
                .build();

        passwordResetRepository.save(resetToken);
        mailService.sendPasswordResetEmail(admin.getEmail(), admin.getFullName(), token);

        return "Password reset link sent to email.";
    }

    // 🔹 RESET PASSWORD (Using token)
    public String resetPassword(ResetPasswordRequest request) throws GenericException {
        PasswordResetToken tokenEntity = passwordResetRepository.findByToken(request.getToken());
        if (tokenEntity == null || tokenEntity.getExpiryDate().before(new Date())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid or expired token.");
        }

        Profile profile = tokenEntity.getProfile();
        profile.setPassword(passwordEncoder.encode(request.getNewPassword()));
        profileRepository.save(profile);

        passwordResetRepository.delete(tokenEntity);
        return "Password reset successfully.";
    }

    // 🔹 CHANGE PASSWORD (with old password)
    public String changePassword(int adminId, ChangePasswordRequest request) throws GenericException {
        Profile admin = profileRepository.findById(adminId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND,"Admin not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), admin.getPassword())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS,"Old password is incorrect.");
        }

        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        profileRepository.save(admin);

        return "Password changed successfully.";
    }

    // 🔹 VALIDATE REGISTRATION REQUEST
    private void validateRegistrationRequest(AdminRegisterRequest request) throws GenericException {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "Email and password are required.");
        }

        if (profileRepository.findByEmail(request.getEmail()) != null) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_PROFILE, "Email already registered.");
        }

        if (request.getPhone() != null && profileRepository.findByPhone(request.getPhone()) != null) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_PROFILE, "Phone already registered.");
        }
    }

    // 🔹 HELPER: Send OTP to user
    private String sendOtpToUser(Profile admin) {
        String otp = generateOtp();
        otpStorageService.saveOtp(admin.getPhone(), otp);
        mailService.sendOtpVerificationEmail(admin.getEmail(), admin.getFullName(), otp);
        return otp;
    }

    // 🔹 HELPER: Generate random 6-digit OTP
    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(1_000_000));
    }

    // 🔹 HELPER: Build Admin Response with JWT Token
    private AdminResponse buildAdminResponse(Profile admin, String token) {
        return AdminResponse.builder()
                .id(admin.getId())
                .fullName(admin.getFullName())
                .email(admin.getEmail())
                .role(admin.getRole())
                .token(token)
                .build();
    }
}
