package com.portfolio.controllers;

import com.portfolio.dtos.AdminLoginRequest;
import com.portfolio.dtos.AdminRegisterRequest;
import com.portfolio.dtos.AdminResponse;
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
}
