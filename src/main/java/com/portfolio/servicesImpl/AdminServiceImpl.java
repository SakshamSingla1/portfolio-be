package com.portfolio.servicesImpl;

import com.portfolio.dtos.*;
import com.portfolio.dtos.ColorTheme.ColorThemeResponseDTO;
import com.portfolio.dtos.NavLinks.NavLinkResponseDTO;
import com.portfolio.entities.*;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.RoleEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.VerificationStatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.OtpStoreRepository;
import com.portfolio.repositories.PasswordResetTokenRepository;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.security.JwtUtil;
import com.portfolio.services.AdminService;
import com.portfolio.services.ColorThemeService;
import com.portfolio.services.NTService;
import com.portfolio.services.NavLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.portfolio.utils.Helper;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final ProfileRepository profileRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final OtpStoreRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final Helper helper;
    private final NTService ntService;
    private final ColorThemeService colorThemeService;
    private final NavLinkService navLinkService;

    @Override
    public AuthResponseDTO register(AuthRegisterDTO registerDTO) throws GenericException {

        if (profileRepository.findByEmail(registerDTO.getEmail()).isPresent())
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_EMAIL, "User with same email already exists");

        if (profileRepository.findByUserName(registerDTO.getUserName()).isPresent())
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_PROFILE, "User with same username already exists");

        if (profileRepository.findByPhone(registerDTO.getPhone()).isPresent())
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_PROFILE, "User with same phone number already exists");

        Profile user = Profile.builder()
                .fullName(registerDTO.getFullName())
                .userName(registerDTO.getUserName())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role("ADMIN")
                .phone(registerDTO.getPhone())
                .status(StatusEnum.ACTIVE)
                .emailVerified(VerificationStatusEnum.PENDING)
                .phoneVerified(VerificationStatusEnum.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        profileRepository.save(user);

        String rawOtp = helper.generateRawOtp();
        String encodedOtp = passwordEncoder.encode(rawOtp);

        otpRepository.deleteByProfileId(user.getId());
        otpRepository.save(
                OtpStore.builder()
                        .profileId(user.getId())
                        .otp(encodedOtp)
                        .createdAt(LocalDateTime.now())
                        .expiryDate(LocalDateTime.now().plusMinutes(10))
                        .build()
        );
        ntService.sendNotification(
                "OTP-VERIFICATION",
                Map.of("name", user.getFullName(), "otp", rawOtp),
                user.getEmail()
        );
        return AuthResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .userName(user.getUserName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .createdAt(user.getCreatedAt())
                .message("User registered successfully. Please verify OTP sent to your email.")
                .build();
    }

    @Override
    public String sendOtp(PhoneOtpRequestDTO dto) throws GenericException {

        Profile user = profileRepository.findByPhone(dto.getPhone())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        String rawOtp = helper.generateRawOtp();
        String encodedOtp = passwordEncoder.encode(rawOtp);

        otpRepository.deleteByProfileId(user.getId());
        otpRepository.save(
                OtpStore.builder()
                        .profileId(user.getId())
                        .otp(encodedOtp)
                        .createdAt(LocalDateTime.now())
                        .expiryDate(LocalDateTime.now().plusMinutes(10))
                        .build()
        );
        ntService.sendNotification(
                "OTP-VERIFICATION",
                Map.of("name", user.getFullName(), "otp", rawOtp),
                user.getEmail()
        );

        return "OTP sent successfully";
    }

    @Override
    public String verifyOtp(OtpRequestDTO dto) throws GenericException {
        Profile profile = profileRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        OtpStore otpStore = otpRepository.findByProfileId(profile.getId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "OTP not found"));
        if (otpStore.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpRepository.deleteByProfileId(profile.getId());
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "OTP expired");
        }
        if (!passwordEncoder.matches(dto.getOtp(), otpStore.getOtp())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid OTP");
        }
        profile.setEmailVerified(VerificationStatusEnum.VERIFIED);
        profile.setPhoneVerified(VerificationStatusEnum.VERIFIED);
        profile.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(profile);
        otpRepository.deleteByProfileId(profile.getId());
        return "OTP verified successfully";
    }

    @Override
    public String resendOtp(String email) throws GenericException {
        Profile user = profileRepository.findByEmail(email)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));
        String rawOtp = helper.generateRawOtp();
        String encodedOtp = passwordEncoder.encode(rawOtp);
        otpRepository.deleteByProfileId(user.getId());
        otpRepository.save(
                OtpStore.builder()
                        .profileId(user.getId())
                        .otp(encodedOtp)
                        .createdAt(LocalDateTime.now())
                        .expiryDate(LocalDateTime.now().plusMinutes(10))
                        .build()
        );
        ntService.sendNotification(
                "OTP-VERIFICATION",
                Map.of("name", user.getFullName(), "otp", rawOtp),
                user.getEmail()
        );
        return "OTP resent successfully";
    }

    @Override
    public String forgotPassword(PasswordResetRequestDTO dto) throws GenericException {

        Profile user = profileRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        String token = UUID.randomUUID().toString();

        passwordResetTokenRepository.deleteByProfileId(user.getId());
        passwordResetTokenRepository.save(
                PasswordResetToken.builder()
                        .profileId(user.getId())
                        .token(token)
                        .expiryDate(LocalDateTime.now().plusMinutes(30))
                        .build()
        );
        ntService.sendNotification(
                "FORGOT-PASSWORD-TOKEN",
                Map.of("name", user.getFullName(), "resetLink", frontendUrl + "?token=" + token),
                user.getEmail()
        );
        return "Password reset link sent";
    }

    @Override
    public String validatePasswordResetToken(String token) throws GenericException {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Token not found"));
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Token expired");
        return "Token is valid";
    }

    @Override
    public String resetPassword(PasswordResetConfirmDTO dto) throws GenericException {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid token"));
        if (token.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Token expired");

        Profile user = profileRepository.findById(token.getProfileId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword()))
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "New password must be different from old password");

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        profileRepository.save(user);
        passwordResetTokenRepository.delete(token);

        return "Password reset successful";
    }

    @Override
    public LoginResponseDTO login(AuthLoginDTO dto) throws GenericException {

        Profile user;

        if (StringUtils.hasText(dto.getEmail())) {
            user = profileRepository.findByEmail(dto.getEmail().trim())
                    .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));
        } else if (StringUtils.hasText(dto.getUsername())) {
            user = profileRepository.findByUserName(dto.getUsername().trim())
                    .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));
        } else {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "Email or username is required");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid password");

        if (user.getEmailVerified() != VerificationStatusEnum.VERIFIED || user.getPhoneVerified() != VerificationStatusEnum.VERIFIED)
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "Account not verified");

        String token = jwtUtil.generateAccessToken(user.getEmail());
        ColorThemeResponseDTO defaultTheme;
        if (user.getThemeName() != null) {
            defaultTheme = colorThemeService.getThemeByName(user.getThemeName());
        } else {
            defaultTheme = colorThemeService.getDefaultTheme();
        }
        List<NavLinkResponseDTO> navLinks = navLinkService.getNavLinks(RoleEnum.valueOf(user.getRole()));

        return LoginResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .userName(user.getUserName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .token("Bearer " + token)
                .defaultTheme(defaultTheme)
                .navLinks(navLinks)
                .build();
    }

    @Override
    public String changePassword(String authorizationHeader, ChangePasswordDTO dto) throws GenericException {

        String email = helper.extractEmailFromHeader(authorizationHeader);

        Profile user = profileRepository.findByEmail(email)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword()))
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Incorrect current password");

        if (!dto.getNewPassword().equals(dto.getConfirmPassword()))
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "Passwords do not match");

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword()))
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "New password must be different from old password");

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        profileRepository.save(user);

        return "Password changed successfully";
    }

}
