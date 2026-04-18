package com.portfolio.services;

import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.dtos.Profile.ProfileRequest;
import com.portfolio.dtos.Profile.ProfileResponse;
import com.portfolio.exceptions.GenericException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileService {
    ProfileResponse get(String id) throws GenericException;
    ProfileResponse update(String id, ProfileRequest request) throws GenericException, IOException;
    ImageUploadResponse uploadProfileImage(String profileId, MultipartFile file) throws IOException, GenericException;
    ImageUploadResponse uploadAboutMeImage(String profileId, MultipartFile file) throws IOException, GenericException;
    ImageUploadResponse uploadLogoImage(String profileId, MultipartFile file) throws IOException, GenericException;
}
