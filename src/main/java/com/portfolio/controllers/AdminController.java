package com.portfolio.controllers;

import com.portfolio.dtos.*;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ---------------- Register ----------------
    @PostMapping("/register")
    public ResponseEntity<ResponseModel<AdminResponse>> register(@RequestBody AdminRegisterRequest request) throws GenericException {
        // First step: save profile, send OTP
        return ApiResponse.successResponse(adminService.register(request), ApiResponse.SUCCESS);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseModel<String>> verifyOtp(@RequestBody VerifyOtpRequest request) throws GenericException {
        adminService.verifyRegistrationOtp(request);
        return ApiResponse.successResponse(null,"OTP verified successfully. User is now active.");
    }

    // ---------------- Login ----------------
    @PostMapping("/login")
    public ResponseEntity<ResponseModel<AdminResponse>> login(@RequestBody AdminLoginRequest request) throws GenericException {
        return ApiResponse.successResponse(adminService.login(request), ApiResponse.SUCCESS);
    }

    // ---------------- Password ----------------
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseModel<String>> forgotPassword(@RequestBody ForgotPasswordRequest email) throws GenericException {
        return ApiResponse.successResponse(adminService.forgotPassword(email), ApiResponse.SUCCESS);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseModel<String>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws GenericException {
        return ApiResponse.successResponse(adminService.resetPassword(resetPasswordRequest), ApiResponse.SUCCESS);
    }

    @PutMapping("/change-password/{id}")
    public ResponseEntity<ResponseModel<String>> changePassword(@PathVariable Integer id, @RequestBody ChangePasswordRequest changePasswordRequest) throws GenericException {
        return ApiResponse.successResponse(adminService.changePassword(id, changePasswordRequest), ApiResponse.SUCCESS);
    }

    // ---------------- Change Email ----------------
    @PutMapping("/change-email/{id}")
    public ResponseEntity<ResponseModel<String>> changeEmail(@PathVariable Integer id, @RequestBody ChangeEmailRequest request) throws GenericException {
        // Step 1: send OTP to new email
        return ApiResponse.successResponse(adminService.requestChangeEmailOtp(id, request), ApiResponse.SUCCESS);
    }

    @PostMapping("/verify-change-email-otp/{id}")
    public ResponseEntity<ResponseModel<String>> verifyChangeEmailOtp(@PathVariable Integer id, @RequestBody VerifyOtpRequest request) throws GenericException {
        // Step 2: verify OTP & update email
        return ApiResponse.successResponse(adminService.verifyChangeEmailOtp(id, request), ApiResponse.SUCCESS);
    }

    // ---------------- Delete Profile ----------------
    @PostMapping("/request-delete-profile-otp/{id}")
    public ResponseEntity<ResponseModel<String>> requestDeleteProfileOtp(@PathVariable Integer id) throws GenericException {
        return ApiResponse.successResponse(adminService.requestDeleteProfileOtp(id), ApiResponse.SUCCESS);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(@PathVariable Integer id, @RequestBody PasswordRequest request) throws GenericException {
        adminService.deleteProfile(id, request);
        return ApiResponse.successResponse("Profile Deleted Successfully", ApiResponse.SUCCESS);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ResponseModel<String>> sendOtp(@RequestBody Map<String, String> request) throws GenericException {
        String mobile = request.get("phone");
        String otp = adminService.sendOtp(mobile);
        return ApiResponse.successResponse(null,"OTP sent successfully to " + mobile);
    }
}
