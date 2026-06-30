package com.portfolio.services;

import com.portfolio.dtos.Admin.*;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.dtos.Profile.ProfileRequest;
import com.portfolio.dtos.Profile.ProfileResponse;
import com.portfolio.dtos.User.UserResponse;
import com.portfolio.entities.Profile;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileService {
    ProfileResponse get(Long id) throws GenericException;
    ProfileResponse update(Long id, ProfileRequest request) throws GenericException, IOException;
    ImageUploadResponse uploadProfileImage(Long profileId, MultipartFile file) throws IOException, GenericException;
    ImageUploadResponse uploadAboutMeImage(Long profileId, MultipartFile file) throws IOException, GenericException;
    ImageUploadResponse uploadLogoImage(Long profileId, MultipartFile file) throws IOException, GenericException;
    
    Page<UserResponse> getAllProfiles(
            Pageable pageable,
            String search,
            String status,
            String role
    );

    UserResponse getUserById(Long id) throws GenericException;
    
    UserResponse updateUserStatus(Long id, StatusUpdateRequest request) throws GenericException;
    
    UserResponse updateUserRole(Long id, RoleUpdateRequest request) throws GenericException;
    
    UserResponse toggleUserVerification(Long id) throws GenericException;
}
