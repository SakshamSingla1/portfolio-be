package com.portfolio.controllers;

import com.portfolio.dtos.*;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ---------------- Register ----------------
    @PostMapping("/register")
    public ResponseEntity<ResponseModel<AdminResponse>> register(@RequestBody AdminRegisterRequest request) throws GenericException {
        return ApiResponse.respond(
                adminService.register(request),
                "Admin registered successfully",
                "Admin registration failed"
        );
    }

    // ---------------- Verify OTP ----------------
    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseModel<String>> verifyOtp(@RequestBody VerifyOtpRequest request) throws GenericException {
        adminService.verifyRegistrationOtp(request);
        return ApiResponse.successResponse(null, "OTP verified successfully. User is now active.");
    }

    // ---------------- Login ----------------
    @PostMapping("/login")
    public ResponseEntity<ResponseModel<AdminResponse>> login(@RequestBody AdminLoginRequest request) throws GenericException {
        return ApiResponse.respond(
                adminService.login(request),
                "Login successful",
                "Login failed"
        );
    }

    // ---------------- Forgot Password ----------------
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseModel<String>> forgotPassword(@RequestBody ForgotPasswordRequest email) throws GenericException {
        return ApiResponse.respond(
                adminService.forgotPassword(email),
                "Forgot password request successful. Check your email",
                "Failed to send forgot password request"
        );
    }

    // ---------------- Reset Password using token ----------------
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseModel<String>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws GenericException {
        return ApiResponse.respond(
                adminService.resetPassword(resetPasswordRequest),
                "Password reset successfully",
                "Password reset failed"
        );
    }

    // ---------------- Change Password using old password ----------------
    @PutMapping("/change-password/{id}")
    public ResponseEntity<ResponseModel<String>> changePassword(@PathVariable Integer id, @RequestBody ChangePasswordRequest changePasswordRequest) throws GenericException {
        return ApiResponse.respond(
                adminService.changePassword(id, changePasswordRequest),
                "Password changed successfully",
                "Failed to change password"
        );
    }

    // ---------------- Change Email ----------------
    @PutMapping("/change-email/{id}")
    public ResponseEntity<ResponseModel<String>> changeEmail(@PathVariable Integer id, @RequestBody ChangeEmailRequest request) throws GenericException {
        return ApiResponse.respond(
                adminService.requestChangeEmailOtp(id, request),
                "OTP sent to new email successfully",
                "Failed to send OTP to new email"
        );
    }

    // ---------------- Change Email Verification ----------------
    @PostMapping("/verify-change-email-otp/{id}")
    public ResponseEntity<ResponseModel<String>> verifyChangeEmailOtp(@PathVariable Integer id, @RequestBody VerifyOtpRequest request) throws GenericException {
        return ApiResponse.respond(
                adminService.verifyChangeEmailOtp(id, request),
                "Email changed successfully",
                "Failed to change email"
        );
    }

    // ---------------- Request Profile Deletion OTP ----------------
    @PostMapping("/request-delete-profile-otp/{id}")
    public ResponseEntity<ResponseModel<String>> requestDeleteProfileOtp(@PathVariable Integer id) throws GenericException {
        return ApiResponse.respond(
                adminService.requestDeleteProfileOtp(id),
                "OTP sent for profile deletion successfully",
                "Failed to send OTP for profile deletion"
        );
    }

    // ---------------- Delete Profile ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(@PathVariable Integer id, @RequestBody PasswordRequest request) throws GenericException {
        adminService.deleteProfile(id, request);
        return ApiResponse.successResponse(null, "Profile deleted successfully");
    }

    // ---------------- Send OTP using phone ----------------
    @PostMapping("/send-otp")
    public ResponseEntity<ResponseModel<String>> sendOtp(@RequestBody Map<String, String> request) throws GenericException {
        String mobile = request.get("phone");
        String otp = adminService.sendOtp(mobile);
        return ApiResponse.successResponse(null, "OTP sent successfully to " + mobile);
    }
}
