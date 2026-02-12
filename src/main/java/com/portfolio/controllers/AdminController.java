package com.portfolio.controllers;

import com.portfolio.dtos.*;
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

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<ResponseModel<AuthResponseDTO>> register(@RequestBody AuthRegisterDTO registerDTO)
            throws GenericException {
        AuthResponseDTO response = adminService.register(registerDTO);
        return ApiResponse.respond(response, "User registered successfully", "User registration failed");
    }

    @Operation(summary = "Verify OTP sent to user email")
    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseModel<String>> verifyOtp(@RequestBody OtpRequestDTO otpRequestDTO)
            throws GenericException {
        String message = adminService.verifyOtp(otpRequestDTO);
        return ApiResponse.respond(message, "OTP verified successfully", "Invalid or expired OTP");
    }

    @Operation(summary = "Resend OTP to user email")
    @PostMapping("/resend-otp")
    public ResponseEntity<ResponseModel<String>> resendOtp(@RequestBody OtpRequestDTO otpRequestDTO)
            throws GenericException {
        String message = adminService.resendOtp(otpRequestDTO.getEmail());
        return ApiResponse.respond(message, "OTP resent successfully", "Failed to resend OTP");
    }

    @Operation(summary = "Send OTP for phone login")
    @PostMapping("/send-otp")
    public ResponseEntity<ResponseModel<String>> sendLoginOtp(@RequestBody PhoneOtpRequestDTO request)
            throws GenericException {
        String message = adminService.sendOtp(request);
        return ApiResponse.respond(message, "OTP sent successfully", "Failed to send OTP");
    }

    @Operation(summary = "Login user using email/username/password or phone+OTP")
    @PostMapping("/login")
    public ResponseEntity<ResponseModel<LoginResponseDTO>> login(@RequestBody AuthLoginDTO loginDTO)
            throws GenericException {
        LoginResponseDTO response = adminService.login(loginDTO);
        return ApiResponse.respond(response, "Login successful", "Invalid credentials");
    }

    @Operation(summary = "Send password reset token to user email")
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseModel<String>> forgotPassword(@RequestBody PasswordResetRequestDTO requestDTO)
            throws GenericException {
        String message = adminService.forgotPassword(requestDTO);
        return ApiResponse.respond(message, "Password reset email sent successfully", "Failed to send reset email");
    }

    @Operation(summary = "Validate password reset token before setting new password")
    @GetMapping("/validate-reset-token")
    public ResponseEntity<ResponseModel<String>> validateResetToken(@RequestParam String token)
            throws GenericException {
        String message = adminService.validatePasswordResetToken(token);
        return ApiResponse.respond(message, "Token is valid", "Token is invalid or expired");
    }

    @Operation(summary = "Reset password using reset token")
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseModel<String>> resetPassword(@RequestBody PasswordResetConfirmDTO requestDTO)
            throws GenericException {
        String message = adminService.resetPassword(requestDTO);
        return ApiResponse.respond(message, "Password reset successfully", "Failed to reset password");
    }

    @Operation(summary = "Change password (for logged-in user)")
    @PutMapping("/change-password")
    public ResponseEntity<ResponseModel<String>> changePassword(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ChangePasswordDTO requestDTO)
            throws GenericException {
        String message = adminService.changePassword(authorizationHeader,requestDTO);
        return ApiResponse.respond(message, "Password changed successfully", "Failed to change password");
    }

    @Operation(summary = "Request email change (send OTP to new email)")
    @PostMapping("/request-email-change")
    public ResponseEntity<ResponseModel<String>> requestEmailChange(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ChangeEmailRequestDTO requestDTO) throws GenericException {
        String message = adminService.requestEmailChange(authorizationHeader, requestDTO);
        return ApiResponse.respond(message,"OTP sent to new email for verification","Failed to send OTP");
    }

    @Operation(summary = "Verify OTP and update email")
    @PutMapping("/verify-email-change")
    public ResponseEntity<ResponseModel<String>> verifyEmailChange(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody VerifyEmailChangeDTO requestDTO) throws GenericException {
        String message = adminService.verifyEmailChangeOtp(authorizationHeader, requestDTO);
        return ApiResponse.respond(message,"Email updated successfully","Failed to update email");
    }
}
