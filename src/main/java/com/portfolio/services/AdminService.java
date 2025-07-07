package com.portfolio.services;

import com.portfolio.dtos.*;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService implements UserDetailsService {

    private final ProfileRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ProfileRepository profileRepository;

    public AdminService(ProfileRepository adminRepository,
                        PasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil, ProfileRepository profileRepository) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.profileRepository = profileRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Profile admin = profileRepository.findByEmail(email);
        if (admin == null) {
            throw new UsernameNotFoundException("Admin not found with email: " + email);
        }

        return new org.springframework.security.core.userdetails.User(
                admin.getEmail(),
                admin.getPassword(),
                List.of(new SimpleGrantedAuthority("ADMIN"))
        );
    }

    public ResponseEntity<ResponseModel<AdminResponse>> register(AdminRegisterRequest request) throws GenericException {
        if(profileRepository.count() > 0){
            throw new GenericException(ExceptionCodeEnum.ADMIN_ALREADY_EXISTS,"Admin already present");
        }
        Profile admin = Profile.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role("ADMIN")
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Profile saved = profileRepository.save(admin);
        String token = jwtUtil.generateAccessToken(admin);

        AdminResponse response = AdminResponse.builder()
                .id(saved.getId())
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .role(saved.getRole())
                .token(token)
                .build();
        return ApiResponse.successResponse(response,"Admin registered successfully");
    }

    public ResponseEntity<ResponseModel<AdminResponse>> login(AdminLoginRequest request) throws GenericException {
        Profile admin = adminRepository.findByEmail(request.getEmail());
        if (admin == null || !passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Invalid email or password");
        }
        String token = jwtUtil.generateAccessToken(admin);
        AdminResponse response = AdminResponse.builder()
                .id(admin.getId())
                .fullName(admin.getFullName())
                .email(admin.getEmail())
                .role(admin.getRole())
                .token(token)
                .build();
        return ApiResponse.successResponse(response,"Admin logged successfully");
    }
}
