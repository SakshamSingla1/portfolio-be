package com.portfolio.controllers;

import com.portfolio.dtos.Authentication.*;
import com.portfolio.entities.Profile;
import com.portfolio.entities.RefreshToken;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.repositories.RefreshTokenRepository;
import com.portfolio.security.JwtUtil;
import com.portfolio.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final ProfileRepository profileRepository;

    @Operation(summary = "Register a new user", description = "Registers a new user account and sends an OTP to the provided email for verification.")
    @PostMapping("/register")
    public ResponseEntity<ResponseModel<AuthResponseDTO>> register(@Valid @RequestBody AuthRegisterDTO registerDTO)
            throws GenericException {
        AuthResponseDTO response = adminService.register(registerDTO);
        return ApiResponse.respond(response, "User registered successfully", "User registration failed");
    }

    @Operation(summary = "Verify OTP sent to user email", description = "Verifies the OTP sent to the user's email after registration. Returns a success message on valid OTP.")
    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseModel<String>> verifyOtp(@Valid @RequestBody OtpRequestDTO otpRequestDTO)
            throws GenericException {
        String message = adminService.verifyOtp(otpRequestDTO);
        return ApiResponse.respond(message, "OTP verified successfully", "Invalid or expired OTP");
    }

    @Operation(summary = "Resend OTP to user email", description = "Resends a new OTP to the user's registered email address.")
    @PostMapping("/resend-otp")
    public ResponseEntity<ResponseModel<String>> resendOtp(@Valid @RequestBody OtpRequestDTO otpRequestDTO)
            throws GenericException {
        String message = adminService.resendOtp(otpRequestDTO.getEmail());
        return ApiResponse.respond(message, "OTP resent successfully", "Failed to resend OTP");
    }

    @Operation(summary = "Send OTP for phone login", description = "Sends a one-time password to the user's phone number for phone-based login.")
    @PostMapping("/send-otp")
    public ResponseEntity<ResponseModel<String>> sendLoginOtp(@Valid @RequestBody PhoneOtpRequestDTO request)
            throws GenericException {
        String message = adminService.sendOtp(request);
        return ApiResponse.respond(message, "OTP sent successfully", "Failed to send OTP");
    }

    @Operation(summary = "Login user using email/username/password or phone+OTP", description = "Authenticates a user by email/username and password, or by phone and OTP. Returns a JWT access token on success.")
    @PostMapping("/login")
    public ResponseEntity<ResponseModel<LoginResponseDTO>> login(
            @Valid @RequestBody AuthLoginDTO loginDTO,
            HttpServletResponse httpResponse) throws GenericException {
        LoginResponseDTO response = adminService.login(loginDTO);
        // Set cookies only after a fully completed login (not when 2FA is still pending)
        if (!response.isTwoFactorRequired() && response.getToken() != null) {
            String accessToken = response.getToken().replace("Bearer ", "").trim();
            String refreshTokenValue = UUID.randomUUID().toString();
            refreshTokenRepository.save(RefreshToken.builder()
                    .profileId(response.getId())
                    .token(refreshTokenValue)
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .revoked(false)
                    .build());
            setAuthCookies(httpResponse, accessToken, refreshTokenValue);
        }
        return ApiResponse.respond(response, "Login successful", "Invalid credentials");
    }

    @Operation(summary = "Refresh access token", description = "Issues a new access token cookie using the httpOnly refresh token cookie. No request body required.")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshTokenValue = extractCookieValue(request, "refreshToken");
        if (refreshTokenValue == null) {
            return ApiResponse.failureResponse(null, "No refresh token provided");
        }
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshTokenValue).orElse(null);
        if (storedToken == null || storedToken.isRevoked()
                || storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ApiResponse.failureResponse(null, "Invalid or expired refresh token");
        }
        Profile profile = profileRepository.findById(storedToken.getProfileId()).orElse(null);
        if (profile == null) {
            return ApiResponse.failureResponse(null, "User not found");
        }
        String newAccessToken = jwtUtil.generateAccessToken(profile.getEmail(), String.valueOf(profile.getId()));
        // Revoke old refresh token (token rotation)
        refreshTokenRepository.deleteByToken(refreshTokenValue);
        // Issue a new refresh token
        String newRefreshToken = UUID.randomUUID().toString();
        refreshTokenRepository.save(RefreshToken.builder()
                .profileId(storedToken.getProfileId())
                .token(newRefreshToken)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build());
        // Set new access token cookie
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true).secure(true).path("/").maxAge(36000).sameSite("Strict").build();
        // Set new refresh token cookie
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true).secure(true).path("/api/v1/auth/refresh").maxAge(7 * 24 * 3600).sameSite("Strict").build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return ApiResponse.successResponse(null, "Token refreshed successfully");
    }

    @Operation(summary = "Logout user", description = "Clears auth cookies and revokes the refresh token in the database.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // Revoke refresh token from DB
        String refreshTokenValue = extractCookieValue(request, "refreshToken");
        if (refreshTokenValue != null) {
            refreshTokenRepository.deleteByToken(refreshTokenValue);
        }
        // Clear access token cookie
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true).secure(true).path("/").maxAge(0).sameSite("Strict").build();
        // Clear refresh token cookie
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true).secure(true).path("/api/v1/auth/refresh").maxAge(0).sameSite("Strict").build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return ApiResponse.successResponse(null, "Logged out successfully");
    }

    // ─── private helpers ────────────────────────────────────────────────────────

    private void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true).secure(true).path("/").maxAge(36000).sameSite("Strict").build();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true).secure(true).path("/api/v1/auth/refresh").maxAge(7 * 24 * 3600).sameSite("Strict").build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private String extractCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (name.equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    @Operation(summary = "Send password reset token to user email", description = "Sends a password reset link to the user's email address.")
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseModel<String>> forgotPassword(@Valid @RequestBody PasswordResetRequestDTO requestDTO)
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
    public ResponseEntity<ResponseModel<String>> resetPassword(@Valid @RequestBody PasswordResetConfirmDTO requestDTO)
            throws GenericException {
        String message = adminService.resetPassword(requestDTO);
        return ApiResponse.respond(message, "Password reset successfully", "Failed to reset password");
    }

    @Operation(summary = "Change password (for logged-in user)", description = "Changes the password for the currently authenticated user. Requires the old password for verification.")
    @PutMapping("/change-password")
    public ResponseEntity<ResponseModel<String>> changePassword(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody ChangePasswordDTO requestDTO)
            throws GenericException {
        String message = adminService.changePassword(authorizationHeader,requestDTO);
        return ApiResponse.respond(message, "Password changed successfully", "Failed to change password");
    }

    @Operation(summary = "Request email change (send OTP to new email)", description = "Sends an OTP to the new email address to verify ownership before committing the email change.")
    @PostMapping("/request-email-change")
    public ResponseEntity<ResponseModel<String>> requestEmailChange(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody ChangeEmailRequestDTO requestDTO) throws GenericException {
        String message = adminService.requestEmailChange(authorizationHeader, requestDTO);
        return ApiResponse.respond(message,"OTP sent to new email for verification","Failed to send OTP");
    }

    @Operation(summary = "Verify OTP and update email", description = "Verifies the OTP sent to the new email and updates the user's email address.")
    @PutMapping("/verify-email-change")
    public ResponseEntity<ResponseModel<String>> verifyEmailChange(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody VerifyEmailChangeDTO requestDTO) throws GenericException {
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
            @Valid @RequestBody TwoFactorVerifyDTO requestDTO,
            HttpServletResponse httpResponse) throws GenericException {
        LoginResponseDTO response = adminService.verify2Fa(requestDTO);
        if (response != null && response.getToken() != null) {
            String accessToken = response.getToken().replace("Bearer ", "").trim();
            String refreshTokenValue = UUID.randomUUID().toString();
            refreshTokenRepository.save(RefreshToken.builder()
                    .profileId(response.getId())
                    .token(refreshTokenValue)
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .revoked(false)
                    .build());
            setAuthCookies(httpResponse, accessToken, refreshTokenValue);
        }
        return ApiResponse.respond(response, "2FA verified successfully", "Failed to verify 2FA");
    }

    @Operation(summary = "Enable or disable 2FA for the authenticated user", description = "Enables or disables two-factor authentication for the authenticated user. Requires a valid TOTP code.")
    @PutMapping("/2fa/toggle")
    public ResponseEntity<ResponseModel<String>> toggle2Fa(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody TotpCodeDTO dto) throws GenericException {
        String message = adminService.toggle2Fa(authorizationHeader, dto.getTotpCode());
        return ApiResponse.respond(message, "2FA toggled successfully", "Failed to toggle 2FA");
    }
}
