package com.portfolio.servicesImpl;

import com.portfolio.dtos.ProfileRequest;
import com.portfolio.dtos.ProfileResponse;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileResponse get(String id) throws GenericException {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        return mapToResponse(profile);
    }

    public ProfileResponse update(String id, ProfileRequest req) throws GenericException {
        Profile existing = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        profileRepository.findByEmail(req.getEmail())
                .ifPresent(owner -> {
                    if (!owner.getId().equals(id)) {
                        try {
                            throw new GenericException(ExceptionCodeEnum.DUPLICATE_PROFILE, "Email already in use");
                        } catch (GenericException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
        profileRepository.findByPhone(req.getPhone())
                .ifPresent(owner -> {
                    if (!owner.getId().equals(id)) {
                        try {
                            throw new GenericException(ExceptionCodeEnum.DUPLICATE_PROFILE, "Phone already in use");
                        } catch (GenericException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
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
        existing.setLogoUrl(req.getLogoUrl());
        existing.setUpdatedAt(LocalDateTime.now());
        Profile updated = profileRepository.save(existing);
        return mapToResponse(updated);
    }

    private ProfileResponse mapToResponse(Profile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .fullName(profile.getFullName())
                .userName(profile.getUserName())
                .title(profile.getTitle())
                .aboutMe(profile.getAboutMe())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .location(profile.getLocation())
                .githubUrl(profile.getGithubUrl())
                .linkedinUrl(profile.getLinkedinUrl())
                .websiteUrl(profile.getWebsiteUrl())
                .profileImageUrl(profile.getProfileImageUrl())
                .logoUrl(profile.getLogoUrl())
                .build();
    }
}
