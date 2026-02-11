package com.portfolio.services;

import com.portfolio.dtos.*;
import com.portfolio.exceptions.GenericException;
import org.springframework.transaction.annotation.Transactional;

public interface AdminService {
    AuthResponseDTO register(AuthRegisterDTO registerDTO) throws GenericException;

    String sendOtp(PhoneOtpRequestDTO requestDTO) throws GenericException;

    String verifyOtp(OtpRequestDTO otpRequestDTO) throws GenericException;

    String resendOtp(String email) throws GenericException;

    LoginResponseDTO login(AuthLoginDTO loginDTO) throws GenericException;

    String forgotPassword(PasswordResetRequestDTO passwordResetRequestDTO) throws GenericException;

    String validatePasswordResetToken(String token) throws GenericException;

    String resetPassword(PasswordResetConfirmDTO dto) throws GenericException;

    String changePassword(String authorizationHeader,ChangePasswordDTO dto) throws GenericException;

    String requestEmailChange(String authorizationHeader,ChangeEmailRequestDTO dto) throws GenericException;

    @Transactional
    String verifyEmailChangeOtp(String authorizationHeader, VerifyEmailChangeDTO dto) throws GenericException;
}

