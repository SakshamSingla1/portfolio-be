package com.portfolio.services;

import com.portfolio.dtos.Admin.AdminCreateUserRequest;
import com.portfolio.dtos.Authentication.*;
import com.portfolio.exceptions.GenericException;
import org.springframework.transaction.annotation.Transactional;

public interface AdminService {
    AuthResponseDTO register(AuthRegisterDTO registerDTO) throws GenericException;

    /**
     * Creates a fully-active user account directly (no OTP step) — the admin creating
     * the account is vouching for it, so email/phone are marked verified immediately.
     * Mirrors the same activation setup {@link #verifyOtp} performs after self-registration
     * (default theme mapping, portfolio social link, welcome notification) so the account
     * isn't missing anything a normally-onboarded user would have.
     * Returns the new profile's ID.
     */
    Long createUserByAdmin(AdminCreateUserRequest dto) throws GenericException;

    String sendOtp(PhoneOtpRequestDTO requestDTO) throws GenericException;

    String verifyOtp(OtpRequestDTO otpRequestDTO) throws GenericException;

    String resendOtp(String email) throws GenericException;

    LoginResponseDTO login(AuthLoginDTO loginDTO) throws GenericException;

    String forgotPassword(PasswordResetRequestDTO passwordResetRequestDTO) throws GenericException;

    String validatePasswordResetToken(String token) throws GenericException;

    String resetPassword(PasswordResetConfirmDTO dto) throws GenericException;

    String changePassword(String authorizationHeader, ChangePasswordDTO dto) throws GenericException;

    String requestEmailChange(String authorizationHeader, ChangeEmailRequestDTO dto) throws GenericException;

    @Transactional
    String verifyEmailChangeOtp(String authorizationHeader, VerifyEmailChangeDTO dto) throws GenericException;

    TwoFactorSetupResponseDTO generate2FaSecret(String authorizationHeader) throws GenericException;

    LoginResponseDTO verify2Fa(TwoFactorVerifyDTO dto) throws GenericException;

    String toggle2Fa(String authHeader, String totpCode) throws GenericException;
}

