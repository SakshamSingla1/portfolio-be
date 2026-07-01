package com.portfolio.controllers;

import com.portfolio.dtos.Authentication.*;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Register a new user", description = "Registers a new user account and sends an OTP to the provided email for verification.")
    @PostMapping("/register")
    public ResponseEntity<ResponseModel<AuthResponseDTO>> register(@RequestBody AuthRegisterDTO registerDTO)
            throws GenericException {
        AuthResponseDTO response = adminService.register(registerDTO);
        return ApiResponse.respond(response, "User registered successfully", "User registration failed");
    }

    @Operation(summary = "Verify OTP sent to user email", description = "Verifies the OTP sent to the user's email after registration. Returns a success message on valid OTP.")
    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseModel<String>> verifyOtp(@RequestBody OtpRequestDTO otpRequestDTO)
            throws GenericException {
        String message = adminService.verifyOtp(otpRequestDTO);
        return ApiResponse.respond(message, "OTP verified successfully", "Invalid or expired OTP");
    }

    @Operation(summary = "Resend OTP to user email", description = "Resends a new OTP to the user's registered email address.")
    @PostMapping("/resend-otp")
    public ResponseEntity<ResponseModel<String>> resendOtp(@RequestBody OtpRequestDTO otpRequestDTO)
            throws GenericException {
        String message = adminService.resendOtp(otpRequestDTO.getEmail());
        return ApiResponse.respond(message, "OTP resent successfully", "Failed to resend OTP");
    }

    @Operation(summary = "Send OTP for phone login", description = "Sends a one-time password to the user's phone number for phone-based login.")
    @PostMapping("/send-otp")
    public ResponseEntity<ResponseModel<String>> sendLoginOtp(@RequestBody PhoneOtpRequestDTO request)
            throws GenericException {
        String message = adminService.sendOtp(request);
        return ApiResponse.respond(message, "OTP sent successfully", "Failed to send OTP");
    }

    @Operation(summary = "Login user using email/username/password or phone+OTP", description = "Authenticates a user by email/username and password, or by phone and OTP. Returns a JWT access token on success.")
    @PostMapping("/login")
    public ResponseEntity<ResponseModel<LoginResponseDTO>> login(@RequestBody AuthLoginDTO loginDTO)
            throws GenericException {
        LoginResponseDTO response = adminService.login(loginDTO);
        return ApiResponse.respond(response, "Login successful", "Invalid credentials");
    }

    @Operation(summary = "Send password reset token to user email", description = "Sends a password reset link to the user's email address.")
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseModel<String>> forgotPassword(@RequestBody PasswordResetRequestDTO requestDTO)
            throws GenericException {
        String message = adminService.forgotPassword(requestDTO);
        return ApiResponse.respond(message, "Password reset email sent successfully", "Failed to send reset email");
    }

    @Operation(summary = "Validate password reset token before setting new password", description = "Checks whether a password reset token is still valid before allowing the user to submit a new password.")
    @GetMapping("/validate-reset-token")
    public ResponseEntity<ResponseModel<String>> validateResetToken(@RequestParam String token)
            throws GenericException {
        String message = adminService.validatePasswordResetToken(token);
        return ApiResponse.respond(message, "Token is valid", "Token is invalid or expired");
    }

    @Operation(summary = "Reset password using reset token", description = "Resets the user's password using the token received via email.")
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseModel<String>> resetPassword(@RequestBody PasswordResetConfirmDTO requestDTO)
            throws GenericException {
        String message = adminService.resetPassword(requestDTO);
        return ApiResponse.respond(message, "Password reset successfully", "Failed to reset password");
    }

    @Operation(summary = "Change password (for logged-in user)", description = "Changes the password for the currently authenticated user. Requires the old password for verification.")
    @PutMapping("/change-password")
    public ResponseEntity<ResponseModel<String>> changePassword(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ChangePasswordDTO requestDTO)
            throws GenericException {
        String message = adminService.changePassword(authorizationHeader,requestDTO);
        return ApiResponse.respond(message, "Password changed successfully", "Failed to change password");
    }

    @Operation(summary = "Request email change (send OTP to new email)", description = "Sends an OTP to the new email address to verify ownership before committing the email change.")
    @PostMapping("/request-email-change")
    public ResponseEntity<ResponseModel<String>> requestEmailChange(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ChangeEmailRequestDTO requestDTO) throws GenericException {
        String message = adminService.requestEmailChange(authorizationHeader, requestDTO);
        return ApiResponse.respond(message,"OTP sent to new email for verification","Failed to send OTP");
    }

    @Operation(summary = "Verify OTP and update email", description = "Verifies the OTP sent to the new email and updates the user's email address.")
    @PutMapping("/verify-email-change")
    public ResponseEntity<ResponseModel<String>> verifyEmailChange(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody VerifyEmailChangeDTO requestDTO) throws GenericException {
        String message = adminService.verifyEmailChangeOtp(authorizationHeader, requestDTO);
        return ApiResponse.respond(message,"Email updated successfully","Failed to update email");
    }

    @Operation(summary = "Generate secret + QR URI", description = "Generates a TOTP secret and QR code URI for configuring an authenticator app.")
    @PostMapping("/2fa/setup")
    public ResponseEntity<ResponseModel<TwoFactorSetupResponseDTO>> generate2FaSecret(
            @RequestHeader("Authorization") String authorizationHeader) throws GenericException {
        TwoFactorSetupResponseDTO response = adminService.generate2FaSecret(authorizationHeader);
        return ApiResponse.respond(response, "Secret and QR URI generated successfully", "Failed to generate secret and QR URI");
    }

    @Operation(summary = "Verify TOTP code for 2FA and return access token", description = "Validates a TOTP code and returns a full access token once 2FA is confirmed.")
    @PostMapping("/2fa/verify")
    public ResponseEntity<ResponseModel<LoginResponseDTO>> verify2Fa(
            @RequestBody  TwoFactorVerifyDTO requestDTO) throws GenericException {
        LoginResponseDTO response = adminService.verify2Fa(requestDTO);
        return ApiResponse.respond(response, "2FA verified successfully", "Failed to verify 2FA");
    }

    @Operation(summary = "Enable or disable 2FA for the authenticated user", description = "Enables or disables two-factor authentication for the authenticated user. Requires a valid TOTP code.")
    @PutMapping("/2fa/toggle")
    public ResponseEntity<ResponseModel<String>> toggle2Fa(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody TotpCodeDTO dto) throws GenericException {
        String message = adminService.toggle2Fa(authorizationHeader, dto.getTotpCode());
        return ApiResponse.respond(message, "2FA toggled successfully", "Failed to toggle 2FA");
    }
}
