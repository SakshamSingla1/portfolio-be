package com.portfolio.controllers;

import com.portfolio.dtos.*;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin Controller", description = "Endpoints for Admin registration, authentication, and account management")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ---------------- Register ----------------
    @Operation(summary = "Register a new Admin", description = "Registers a new admin user with provided details")
    @PostMapping("/register")
    public ResponseEntity<ResponseModel<AdminResponse>> register(@RequestBody AdminRegisterRequest request) throws GenericException {
        return ApiResponse.respond(
                adminService.register(request),
                "Admin registered successfully",
                "Admin registration failed"
        );
    }

    // ---------------- Verify OTP ----------------
    @Operation(summary = "Verify Registration OTP", description = "Verifies OTP sent to admin during registration")
    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseModel<String>> verifyOtp(@RequestBody VerifyOtpRequest request) throws GenericException {
        adminService.verifyRegistrationOtp(request);
        return ApiResponse.successResponse(null, "OTP verified successfully. User is now active.");
    }

    // ---------------- Login ----------------
    @Operation(summary = "Login Admin", description = "Logs in an admin using email/username and password")
    @PostMapping("/login")
    public ResponseEntity<ResponseModel<AdminResponse>> login(@RequestBody AdminLoginRequest request) throws GenericException {
        return ApiResponse.respond(
                adminService.login(request),
                "Login successful",
                "Login failed"
        );
    }

    // ---------------- Forgot Password ----------------
    @Operation(summary = "Forgot Password", description = "Sends reset link/OTP to registered email for password recovery")
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseModel<String>> forgotPassword(@RequestBody ForgotPasswordRequest email) throws GenericException {
        return ApiResponse.respond(
                adminService.forgotPassword(email),
                "Forgot password request successful. Check your email",
                "Failed to send forgot password request"
        );
    }

    // ---------------- Reset Password ----------------
    @Operation(summary = "Reset Password", description = "Resets admin password using reset token/OTP")
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseModel<String>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws GenericException {
        return ApiResponse.respond(
                adminService.resetPassword(resetPasswordRequest),
                "Password reset successfully",
                "Password reset failed"
        );
    }

    // ---------------- Change Password ----------------
    @Operation(summary = "Change Password", description = "Changes password using old password for authentication")
    @PutMapping("/change-password/{id}")
    public ResponseEntity<ResponseModel<String>> changePassword(@PathVariable Integer id, @RequestBody ChangePasswordRequest changePasswordRequest) throws GenericException {
        return ApiResponse.respond(
                adminService.changePassword(id, changePasswordRequest),
                "Password changed successfully",
                "Failed to change password"
        );
    }

    // ---------------- Change Email ----------------
    @Operation(summary = "Request Change Email", description = "Requests an OTP to change admin email")
    @PutMapping("/change-email/{id}")
    public ResponseEntity<ResponseModel<String>> changeEmail(@PathVariable Integer id, @RequestBody ChangeEmailRequest request) throws GenericException {
        return ApiResponse.respond(
                adminService.requestChangeEmailOtp(id, request),
                "OTP sent to new email successfully",
                "Failed to send OTP to new email"
        );
    }

    // ---------------- Verify Change Email ----------------
    @Operation(summary = "Verify Change Email OTP", description = "Verifies OTP for changing admin email")
    @PostMapping("/verify-change-email-otp/{id}")
    public ResponseEntity<ResponseModel<String>> verifyChangeEmailOtp(@PathVariable Integer id, @RequestBody VerifyOtpRequest request) throws GenericException {
        return ApiResponse.respond(
                adminService.verifyChangeEmailOtp(id, request),
                "Email changed successfully",
                "Failed to change email"
        );
    }

    // ---------------- Request Delete Profile OTP ----------------
    @Operation(summary = "Request Profile Deletion OTP", description = "Sends OTP to confirm admin profile deletion")
    @PostMapping("/request-delete-profile-otp/{id}")
    public ResponseEntity<ResponseModel<String>> requestDeleteProfileOtp(@PathVariable Integer id) throws GenericException {
        return ApiResponse.respond(
                adminService.requestDeleteProfileOtp(id),
                "OTP sent for profile deletion successfully",
                "Failed to send OTP for profile deletion"
        );
    }

    // ---------------- Delete Profile ----------------
    @Operation(summary = "Delete Admin Profile", description = "Deletes admin profile permanently after OTP verification")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(@PathVariable Integer id, @RequestBody PasswordRequest request) throws GenericException {
        adminService.deleteProfile(id, request);
        return ApiResponse.successResponse(null, "Profile deleted successfully");
    }

    // ---------------- Send OTP using phone ----------------
    @Operation(summary = "Send OTP via Phone", description = "Sends OTP to given phone number for verification")
    @PostMapping("/send-otp")
    public ResponseEntity<ResponseModel<String>> sendOtp(@RequestBody Map<String, String> request) throws GenericException {
        String mobile = request.get("phone");
        String otp = adminService.sendOtp(mobile);
        return ApiResponse.successResponse(null, "OTP sent successfully to " + mobile);
    }
}
