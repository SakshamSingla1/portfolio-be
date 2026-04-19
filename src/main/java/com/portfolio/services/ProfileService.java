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
    ProfileResponse get(String id) throws GenericException;
    ProfileResponse update(String id, ProfileRequest request) throws GenericException, IOException;
    ImageUploadResponse uploadProfileImage(String profileId, MultipartFile file) throws IOException, GenericException;
    ImageUploadResponse uploadAboutMeImage(String profileId, MultipartFile file) throws IOException, GenericException;
    ImageUploadResponse uploadLogoImage(String profileId, MultipartFile file) throws IOException, GenericException;
    
    Page<UserResponse> getAllProfiles(
            Pageable pageable,
            String search,
            StatusEnum status,
            String role,
            String sortBy,
            String sortDir
    );

    UserResponse getUserById(String id) throws GenericException;
    
    UserResponse updateUserStatus(String id, StatusUpdateRequest request) throws GenericException;
    
    UserResponse updateUserRole(String id, RoleUpdateRequest request) throws GenericException;
    
    UserResponse toggleUserVerification(String id) throws GenericException;
}
