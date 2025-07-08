package com.portfolio.controllers;

import com.portfolio.dtos.*;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/register")
    public ResponseEntity<ResponseModel<AdminResponse>> register(@RequestBody AdminRegisterRequest request) throws GenericException {
        return adminService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseModel<AdminResponse>> login(@RequestBody AdminLoginRequest request) throws GenericException {
        return adminService.login(request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseModel<String>> forgotPassword(@RequestBody ForgotPasswordRequest email) throws GenericException {
        return adminService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseModel<String>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest ) throws GenericException {
        return adminService.resetPassword(resetPasswordRequest);
    }
}
