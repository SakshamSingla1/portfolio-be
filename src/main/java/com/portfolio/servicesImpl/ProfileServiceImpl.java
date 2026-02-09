package com.portfolio.servicesImpl;

import com.portfolio.dtos.ImageUploadResponse;
import com.portfolio.dtos.ProfileRequest;
import com.portfolio.dtos.ProfileResponse;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.services.CloudinaryService;
import com.portfolio.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public ProfileResponse get(String id) throws GenericException {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        return mapToResponse(profile);
    }

    @Override
    public ProfileResponse update(String id, ProfileRequest req) throws GenericException, IOException {

        Profile existing = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        profileRepository.findByEmail(req.getEmail())
                .ifPresent(owner -> {
                    if (!owner.getId().equals(id)) {
                        throw new RuntimeException(new GenericException(ExceptionCodeEnum.DUPLICATE_PROFILE, "Email already in use")
                        );
                    }
                });
        profileRepository.findByPhone(req.getPhone())
                .ifPresent(owner -> {
                    if (!owner.getId().equals(id)) {
                        throw new RuntimeException(new GenericException(ExceptionCodeEnum.DUPLICATE_PROFILE, "Phone already in use"));
                    }
                });
        existing.setFullName(req.getFullName());
        existing.setTitle(req.getTitle());
        existing.setAboutMe(req.getAboutMe());
        existing.setEmail(req.getEmail());
        existing.setPhone(req.getPhone());
        existing.setLocation(req.getLocation());
        if (!Objects.equals(existing.getProfileImagePublicId(), req.getProfileImagePublicId())) {
            if (existing.getProfileImagePublicId() != null) {
                cloudinaryService.deleteFile(existing.getProfileImagePublicId());
            }
            existing.setProfileImageUrl(req.getProfileImageUrl());
            existing.setProfileImagePublicId(req.getProfileImagePublicId());
        }
        if (!Objects.equals(existing.getAboutMeImageUrl(), req.getAboutMeImagePublicId())) {
            if (existing.getAboutMeImageUrl() != null) {
                cloudinaryService.deleteFile(existing.getAboutMeImagePublicId());
            }
            existing.setAboutMeImageUrl(req.getAboutMeImageUrl());
            existing.setAboutMeImagePublicId(req.getAboutMeImagePublicId());
        }
        if (!Objects.equals(existing.getLogoPublicId(), req.getLogoPublicId())) {
            if (existing.getLogoPublicId() != null) {
                cloudinaryService.deleteFile(existing.getLogoPublicId());
            }
            existing.setLogoUrl(req.getLogoUrl());
            existing.setLogoPublicId(req.getLogoPublicId());
        }
        existing.setThemeName(req.getThemeName());
        existing.setUpdatedAt(LocalDateTime.now());
        Profile updated = profileRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public ImageUploadResponse uploadProfileImage(
            String profileId,
            MultipartFile file
    ) throws IOException, GenericException {
        profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        Map uploadResult = cloudinaryService.uploadProfileImage(file);
        return new ImageUploadResponse(uploadResult.get("secure_url").toString(), uploadResult.get("public_id").toString());
    }

    @Override
    public ImageUploadResponse uploadAboutMeImage(
            String profileId,
            MultipartFile file
    ) throws IOException, GenericException {
        profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        Map uploadResult = cloudinaryService.uploadProfileImage(file);
        return new ImageUploadResponse(uploadResult.get("secure_url").toString(), uploadResult.get("public_id").toString());
    }

    @Override
    public ImageUploadResponse uploadLogoImage(
            String profileId,
            MultipartFile file
    ) throws IOException, GenericException {
        profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        Map uploadResult = cloudinaryService.uploadLogoImage(file);
        return new ImageUploadResponse(uploadResult.get("secure_url").toString(), uploadResult.get("public_id").toString());
    }

    // -------------------- MAPPER --------------------
    private ProfileResponse mapToResponse(Profile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .fullName(profile.getFullName())
                .userName(profile.getUserName())
                .title(profile.getTitle())
                .aboutMe(profile.getAboutMe())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .role(profile.getRole())
                .status(profile.getStatus())
                .location(profile.getLocation())
                .profileImageUrl(profile.getProfileImageUrl())
                .profileImagePublicId(profile.getProfileImagePublicId())
                .aboutMeImageUrl(profile.getAboutMeImageUrl())
                .aboutMeImagePublicId(profile.getAboutMeImagePublicId())
                .logoUrl(profile.getLogoUrl())
                .logoPublicId(profile.getLogoPublicId())
                .themeName(profile.getThemeName())
                .emailVerified(profile.getEmailVerified())
                .phoneVerified(profile.getPhoneVerified())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
