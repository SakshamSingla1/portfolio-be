package com.portfolio.servicesImpl;

import com.portfolio.dao.authentication.OtpStoreDao;
import com.portfolio.dao.authentication.PasswordResetTokenDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dao.profile_theme.ProfileThemeMappingDao;
import com.portfolio.dtos.Authentication.*;
import com.portfolio.dtos.ColorTheme.ColorThemeResponseDTO;
import com.portfolio.dtos.Role.RolePermissionResponseDTO;
import com.portfolio.dtos.SocialLinks.SocialLinkRequestDTO;
import com.portfolio.entities.*;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.VerificationStatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.security.JwtUtil;
import com.portfolio.services.*;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.portfolio.utils.EncryptionUtil;
import com.portfolio.utils.Helper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final int OTP_TTL_MINUTES = 10;

    @Value("${app.url}")
    private String url;

    private final ProfileDao profileDao;
    private final PasswordResetTokenDao passwordResetTokenDao;
    private final OtpStoreDao otpStoreDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final Helper helper;
    private final NTService ntService;
    private final ColorThemeService colorThemeService;
    private final RoleService roleService;
    private final ProfileThemeMappingDao profileThemeMappingDao;
    private final SocialLinkService socialLinkService;
    private final EncryptionUtil encryptionUtil;

    @Override
    @Transactional
    public AuthResponseDTO register(AuthRegisterDTO registerDTO) throws GenericException {

        if (profileDao.existsByEmail(registerDTO.getEmail()))
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_EMAIL, "User with same email already exists");

        if (profileDao.existsByUserName(registerDTO.getUserName()))
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_PROFILE, "User with same username already exists");

        if (profileDao.existsByPhone(registerDTO.getPhone()))
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_PROFILE, "User with same phone number already exists");

        Profile user = Profile.builder()
                .fullName(registerDTO.getFullName())
                .userName(registerDTO.getUserName())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .roleId(2L)
                .phone(registerDTO.getPhone())
                .status(StatusEnum.INACTIVE)
                .emailVerified(VerificationStatusEnum.PENDING)
                .phoneVerified(VerificationStatusEnum.PENDING)
                .build();
        profileDao.save(user);

        String rawOtp = helper.generateRawOtp();
        String encodedOtp = passwordEncoder.encode(rawOtp);

        otpStoreDao.deleteByProfileId(user.getId());
        otpStoreDao.save(
                OtpStore.builder()
                        .profileId(user.getId())
                        .otp(encodedOtp)
                        .expiryDate(LocalDateTime.now().plusMinutes(OTP_TTL_MINUTES))
                        .build()
        );
        ntService.sendNotification(
                "OTP-VERIFICATION",
                Map.of(
                        "fullName", user.getFullName(),
                        "otp", rawOtp,
                        "expiryMinutes", OTP_TTL_MINUTES
                ),
                user.getEmail()
        );
        return AuthResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .userName(user.getUserName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roleId(user.getRoleId())
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .createdAt(user.getCreatedAt())
                .message("User registered successfully. Please verify OTP sent to your email.")
                .build();
    }

    @Override
    @Transactional
    public String sendOtp(PhoneOtpRequestDTO dto) throws GenericException {

        Profile user = profileDao.findByPhone(dto.getPhone())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        String rawOtp = helper.generateRawOtp();
        String encodedOtp = passwordEncoder.encode(rawOtp);

        otpStoreDao.deleteByProfileId(user.getId());
        otpStoreDao.save(
                OtpStore.builder()
                        .profileId(user.getId())
                        .otp(encodedOtp)
                        .expiryDate(LocalDateTime.now().plusMinutes(OTP_TTL_MINUTES))
                        .build()
        );
        ntService.sendNotification(
                "OTP-VERIFICATION",
                Map.of(
                        "fullName", user.getFullName(),
                        "otp", rawOtp,
                        "expiryMinutes", OTP_TTL_MINUTES
                ),
                user.getEmail()
        );

        return "OTP sent successfully";
    }

    @Override
    @Transactional
    public String verifyOtp(OtpRequestDTO dto) throws GenericException {
        Profile profile = profileDao.findByEmail(dto.getEmail())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        OtpStore otpStore = otpStoreDao.findByProfileId(profile.getId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "OTP not found"));
        if (otpStore.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpStoreDao.deleteByProfileId(profile.getId());
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "OTP expired");
        }
        if (otpStore.getAttempts() >= 5) {
            otpStoreDao.deleteByProfileId(profile.getId());
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "Too many failed attempts. Please request a new OTP.");
        }
        if (!passwordEncoder.matches(dto.getOtp(), otpStore.getOtp())) {
            otpStore.setAttempts(otpStore.getAttempts() + 1);
            otpStoreDao.save(otpStore);
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid OTP");
        }
        profile.setEmailVerified(VerificationStatusEnum.VERIFIED);
        profile.setStatus(StatusEnum.ACTIVE);
        profile.setUpdatedAt(LocalDateTime.now());
        profileDao.save(profile);
        otpStoreDao.deleteByProfileId(profile.getId());

        try {
            if (profileThemeMappingDao.findByProfileId(profile.getId()).isEmpty()) {
                profileThemeMappingDao.save(
                        ProfileThemeMapping.builder()
                                .profileId(profile.getId())
                                .themeId(1L)
                                .build()
                );
            }
        } catch (Exception e) {
            log.error("Failed to create theme mapping for profile {}: {}", profile.getId(), e.getMessage(), e);
        }

        try {
            socialLinkService.createLink(
                    SocialLinkRequestDTO.builder()
                            .profileId(profile.getId())
                            .platform(PlatformEnum.PORTFOLIO)
                            .url("https://" + profile.getUserName() + ".portfoliosbuilder.com/")
                            .order("1")
                            .status(StatusEnum.ACTIVE)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to create portfolio social link for profile {}: {}", profile.getId(), e.getMessage(), e);
        }

        return "OTP verified successfully";
    }

    @Override
    @Transactional
    public String resendOtp(String email) throws GenericException {
        Profile user = profileDao.findByEmail(email)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));
        String rawOtp = helper.generateRawOtp();
        String encodedOtp = passwordEncoder.encode(rawOtp);
        otpStoreDao.deleteByProfileId(user.getId());
        otpStoreDao.save(
                OtpStore.builder()
                        .profileId(user.getId())
                        .otp(encodedOtp)
                        .expiryDate(LocalDateTime.now().plusMinutes(OTP_TTL_MINUTES))
                        .build()
        );
        ntService.sendNotification(
                "OTP-VERIFICATION",
                Map.of(
                        "fullName", user.getFullName(),
                        "otp", rawOtp,
                        "expiryMinutes", OTP_TTL_MINUTES
                ),
                user.getEmail()
        );
        return "OTP resent successfully";
    }

    @Override
    @Transactional
    public String forgotPassword(PasswordResetRequestDTO dto) throws GenericException {

        Profile user = profileDao.findByEmail(dto.getEmail())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        String token = UUID.randomUUID().toString();

        passwordResetTokenDao.deleteByProfileId(user.getId());
        passwordResetTokenDao.save(
                PasswordResetToken.builder()
                        .profileId(user.getId())
                        .token(token)
                        .expiryDate(LocalDateTime.now().plusMinutes(30))
                        .build()
        );
        ntService.sendNotification(
                "FORGOT-PASSWORD-TOKEN",
                Map.of("name", user.getFullName(), "resetLink", url + "?token=" + token),
                user.getEmail()
        );
        return "Password reset link sent";
    }

    @Override
    public String validatePasswordResetToken(String token) throws GenericException {
        PasswordResetToken resetToken = passwordResetTokenDao.findByToken(token)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Token not found"));
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Token expired");
        return "Token is valid";
    }

    @Override
    @Transactional
    public String resetPassword(PasswordResetConfirmDTO dto) throws GenericException {
        PasswordResetToken token = passwordResetTokenDao.findByToken(dto.getToken())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid token"));
        if (token.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Token expired");

        Profile user = profileDao.findById(token.getProfileId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword()))
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "New password must be different from old password");

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        profileDao.save(user);
        passwordResetTokenDao.delete(token);

        return "Password reset successful";
    }

    @Override
    @Transactional
    public LoginResponseDTO login(AuthLoginDTO dto) throws GenericException {

        Profile user;

        if (StringUtils.hasText(dto.getPhone()) && StringUtils.hasText(dto.getOtp())) {
            user = profileDao.findByPhone(dto.getPhone().trim())
                    .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

            OtpStore otpStore = otpStoreDao.findByProfileId(user.getId())
                    .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "OTP not found"));

            if (otpStore.getExpiryDate().isBefore(LocalDateTime.now())) {
                throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "OTP expired");
            }

            if (!passwordEncoder.matches(dto.getOtp(), otpStore.getOtp())) {
                throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid OTP");
            }
            otpStoreDao.deleteByProfileId(user.getId());
        } else {
            if (StringUtils.hasText(dto.getEmail())) {
                user = profileDao.findByEmail(dto.getEmail().trim())
                        .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));
            } else if (StringUtils.hasText(dto.getUsername())) {
                user = profileDao.findByUserName(dto.getUsername().trim())
                        .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));
            } else {
                throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "Email or username is required");
            }
            if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
                throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid password");
        }

        if (user.getStatus() != StatusEnum.ACTIVE) {
            throw new GenericException(ExceptionCodeEnum.UNAUTHORIZED, "Account is not active. Please verify your email first.");
        }
        if (user.getEmailVerified() != VerificationStatusEnum.VERIFIED) {
            throw new GenericException(ExceptionCodeEnum.UNAUTHORIZED, "Email is not verified. Please verify your email first.");
        }
        if (user.getPhoneVerified() != VerificationStatusEnum.VERIFIED) {
            throw new GenericException(ExceptionCodeEnum.UNAUTHORIZED, "Phone number is not verified. Please verify your phone number first.");
        }
        if (user.isTwoFactorEnabled()) {
            String pendingToken = jwtUtil.generatePendingToken(user.getEmail(), user.getId());
            return LoginResponseDTO.builder()
                    .twoFactorRequired(true)
                    .pendingToken(pendingToken)
                    .build();
        }

        String token = jwtUtil.generateAccessToken(user.getEmail(), String.valueOf(user.getId()));
        ColorThemeResponseDTO defaultTheme = profileThemeMappingDao.findByProfileId(user.getId())
                .map(mapping -> {
                    try {
                        return colorThemeService.getThemeById(mapping.getThemeId());
                    } catch (GenericException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .orElseGet(() -> {
                    try { return colorThemeService.getDefaultTheme(); }
                    catch (GenericException e) { return null; }
                });
        RolePermissionResponseDTO rolePermissionResponse = roleService.getRolePermissionsByRoleId(user.getRoleId());

        return LoginResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .userName(user.getUserName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roleId(user.getRoleId())
                .roleName(rolePermissionResponse.getName())
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .token("Bearer " + token)
                .defaultTheme(defaultTheme)
                .rolePermissions(rolePermissionResponse)
                .isTwoFactorEnabled(user.isTwoFactorEnabled())
                .build();
    }

    @Override
    @Transactional
    public String changePassword(String authorizationHeader, ChangePasswordDTO dto) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(authorizationHeader);
        Profile user = profileDao.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Incorrect current password");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST,"New password and confirm password do not match");
        }
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST,"New password must be different from old password");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        profileDao.save(user);
        return "Password changed successfully";
    }

    @Transactional
    @Override
    public String requestEmailChange(String authorizationHeader, ChangeEmailRequestDTO dto)throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(authorizationHeader);
        Profile user = profileDao.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));
        if (dto.getNewEmail().equalsIgnoreCase(user.getEmail())) {
            throw new GenericException(
                    ExceptionCodeEnum.BAD_REQUEST,
                    "New email must be different"
            );
        }
        String rawOtp = helper.generateRawOtp();
        String encodedOtp = passwordEncoder.encode(rawOtp);
        otpStoreDao.deleteByProfileId(user.getId());
        otpStoreDao.save(
                OtpStore.builder()
                        .profileId(user.getId())
                        .otp(encodedOtp)
                        .expiryDate(LocalDateTime.now().plusMinutes(5))
                        .build()
        );
        ntService.sendNotification(
                "OTP-VERIFICATION",
                Map.of(
                        "fullName", user.getFullName(),
                        "otp", rawOtp,
                        "expiryMinutes", 5
                ),
                dto.getNewEmail()
        );
        return "OTP sent to new email for verification";
    }

    @Transactional
    @Override
    public String verifyEmailChangeOtp(String authorizationHeader, VerifyEmailChangeDTO dto) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(authorizationHeader);
        Profile user = profileDao.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        OtpStore otpStore = otpStoreDao.findByProfileId(user.getId())
                .orElseThrow(() ->
                        new GenericException(
                                ExceptionCodeEnum.BAD_REQUEST,
                                "No OTP request found"
                        )
                );
        if (otpStore.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpStoreDao.deleteByProfileId(user.getId());
            throw new GenericException(
                    ExceptionCodeEnum.BAD_REQUEST,
                    "OTP expired"
            );
        }
        if (!passwordEncoder.matches(dto.getOtp(), otpStore.getOtp())) {
            throw new GenericException(
                    ExceptionCodeEnum.INVALID_CREDENTIALS,
                    "Invalid OTP"
            );
        }
        if (profileDao.existsByEmail(dto.getNewEmail())) {
            throw new GenericException(
                    ExceptionCodeEnum.BAD_REQUEST,
                    "Email already attached with a profile"
            );
        }
        user.setEmail(dto.getNewEmail());
        user.setUpdatedAt(LocalDateTime.now());
        profileDao.save(user);
        otpStoreDao.deleteByProfileId(user.getId());
        return "Email updated successfully";
    }

    @Override
    @Transactional
    public TwoFactorSetupResponseDTO generate2FaSecret(String authorizationHeader) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(authorizationHeader);
        Profile profile = profileDao.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));
        String secret = new DefaultSecretGenerator().generate();
        profile.setTotpSecret(encryptionUtil.encrypt(secret));
        profileDao.save(profile);
        String otpAuthUrl = new QrData.Builder()
                .label(profile.getEmail())
                .secret(secret)
                .issuer("Portfolio Admin")
                .digits(6)
                .period(30)
                .build()
                .getUri();
        return TwoFactorSetupResponseDTO.builder()
                .secret(secret)
                .otpAuthUrl(otpAuthUrl)
                .build();
    }

    @Override
    public LoginResponseDTO verify2Fa(TwoFactorVerifyDTO dto) throws GenericException {
        if (!jwtUtil.isPendingToken(dto.getPendingToken())) {
            throw new GenericException(ExceptionCodeEnum.UNAUTHORIZED, "Invalid or expired session. Please log in again.");
        }
        String email = jwtUtil.extractEmail(dto.getPendingToken());
        Profile user = profileDao.findByEmail(email)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));
        String decryptedSecret = encryptionUtil.decrypt(user.getTotpSecret());
        boolean valid = new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider())
                .isValidCode(decryptedSecret, dto.getTotpCode());
        if (!valid) {
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid authenticator code. Please try again.");
        }
        String token = jwtUtil.generateAccessToken(user.getEmail(), String.valueOf(user.getId()));
        ColorThemeResponseDTO defaultTheme = profileThemeMappingDao.findByProfileId(user.getId())
                .map(mapping -> {
                    try { return colorThemeService.getThemeById(mapping.getThemeId()); }
                    catch (GenericException e) { return null; }
                })
                .filter(Objects::nonNull)
                .orElseGet(() -> {
                    try { return colorThemeService.getDefaultTheme(); }
                    catch (GenericException e) { return null; }
                });
        RolePermissionResponseDTO rolePermissionResponse = roleService.getRolePermissionsByRoleId(user.getRoleId());
        return LoginResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .userName(user.getUserName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roleId(user.getRoleId())
                .roleName(rolePermissionResponse.getName())
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .token("Bearer " + token)
                .defaultTheme(defaultTheme)
                .rolePermissions(rolePermissionResponse)
                .isTwoFactorEnabled(user.isTwoFactorEnabled())
                .build();
    }

    @Override
    @Transactional
    public String toggle2Fa(String authHeader, String totpCode) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(authHeader);
        Profile profile = profileDao.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));
        if (profile.getTotpSecret() == null) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "2FA not set up. Call /2fa/setup first.");
        }
        String decryptedSecret = encryptionUtil.decrypt(profile.getTotpSecret());
        boolean valid = new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider())
                .isValidCode(decryptedSecret, totpCode);
        if (!valid) {
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid authenticator code. Please try again.");
        }
        if (profile.isTwoFactorEnabled()) {
            profile.setTwoFactorEnabled(false);
            profile.setTotpSecret(null);
            profileDao.save(profile);
            return "Two-factor authentication disabled";
        } else {
            profile.setTwoFactorEnabled(true);
            profileDao.save(profile);
            return "Two-factor authentication enabled";
        }
    }


}
