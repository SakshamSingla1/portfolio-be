package com.portfolio.services;

import com.portfolio.dtos.ProfileRequest;
import com.portfolio.dtos.ProfileResponse;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.repositories.ProfileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public ResponseEntity<ResponseModel<ProfileResponse>> create(ProfileRequest req) throws GenericException {
        Profile existingProfile = profileRepository.findByEmailAndPhone(req.getEmail(), req.getPhone());

        if (existingProfile != null) {
            return ApiResponse.failureResponse(null, "Profile with the same email and phone already exists");
        }

        Profile profile = Profile.builder()
                .fullName(req.getFullName())
                .title(req.getTitle())
                .aboutMe(req.getAboutMe())
                .email(req.getEmail())
                .phone(req.getPhone())
                .location(req.getLocation())
                .githubUrl(req.getGithubUrl())
                .linkedinUrl(req.getLinkedinUrl())
                .websiteUrl(req.getWebsiteUrl())
                .profileImageUrl(req.getProfileImageUrl())
                .logo(req.getLogo())
                .build();

        Profile saved = profileRepository.save(profile);
        return ApiResponse.successResponse(mapToResponse(saved), "Profile saved");
    }

    public ResponseEntity<ResponseModel<ProfileResponse>> get() throws GenericException {
        Optional<Profile> profile = profileRepository.findAll().stream().findFirst();

        if (profile.isEmpty()) {
            return ApiResponse.failureResponse(null, "Profile not found");
        }

        return ApiResponse.successResponse(mapToResponse(profile.get()), "Profile fetched");
    }

    public ResponseEntity<ResponseModel<ProfileResponse>> update(Integer id, ProfileRequest req) throws GenericException {
        Profile existing = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND,"Profile not found"));

        existing.setFullName(req.getFullName());
        existing.setTitle(req.getTitle());
        existing.setAboutMe(req.getAboutMe());
        existing.setEmail(req.getEmail());
        existing.setPhone(req.getPhone());
        existing.setLocation(req.getLocation());
        existing.setGithubUrl(req.getGithubUrl());
        existing.setLinkedinUrl(req.getLinkedinUrl());
        existing.setWebsiteUrl(req.getWebsiteUrl());
        existing.setProfileImageUrl(req.getProfileImageUrl());
        existing.setLogo(req.getLogo());

        Profile updated = profileRepository.save(existing);
        return ApiResponse.successResponse(mapToResponse(updated), "Profile updated");
    }

    private ProfileResponse mapToResponse(Profile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .fullName(profile.getFullName())
                .title(profile.getTitle())
                .aboutMe(profile.getAboutMe())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .location(profile.getLocation())
                .githubUrl(profile.getGithubUrl())
                .linkedinUrl(profile.getLinkedinUrl())
                .websiteUrl(profile.getWebsiteUrl())
                .profileImageUrl(profile.getProfileImageUrl())
                .logo(profile.getLogo())
                .build();
    }
}
