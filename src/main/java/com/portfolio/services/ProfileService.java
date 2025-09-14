package com.portfolio.services;

import com.portfolio.dtos.ProfileRequest;
import com.portfolio.dtos.ProfileResponse;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    // 🔹 GET PROFILE BY ID
    public ProfileResponse get(Integer id) throws GenericException {
        Profile existing = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        return mapToResponse(existing);
    }

    // 🔹 UPDATE PROFILE
    public ProfileResponse update(Integer id, ProfileRequest req) throws GenericException {
        Profile existing = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));

        // Check if email/phone is being updated to another existing profile
        Profile emailOwner = profileRepository.findByEmail(req.getEmail());
        if (emailOwner != null && emailOwner.getId() != id) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_PROFILE, "Email already in use by another profile");
        }

        Profile phoneOwner = profileRepository.findByPhone(req.getPhone());
        if (phoneOwner != null && phoneOwner.getId() != id) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_PROFILE, "Phone already in use by another profile");
        }

        // Update profile fields
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
        return mapToResponse(updated);
    }

    // 🔹 MAP ENTITY TO RESPONSE DTO
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
