package com.portfolio.servicesImpl;

import com.portfolio.dtos.Admin.RoleUpdateRequest;
import com.portfolio.dtos.Admin.StatusUpdateRequest;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.dtos.Profile.ProfileRequest;
import com.portfolio.dtos.Profile.ProfileResponse;
import com.portfolio.dtos.User.UserResponse;
import com.portfolio.entities.Profile;
import com.portfolio.entities.Role;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.VerificationStatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.repositories.RoleRepository;
import com.portfolio.services.CloudinaryService;
import com.portfolio.services.ProfileService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final CloudinaryService cloudinaryService;
    private final Helper helper;
    private final RoleRepository roleRepository;

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
                .roleId(profile.getRoleId())
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
                .build();
    }

    @Override
    public Page<UserResponse> getAllProfiles(
            Pageable pageable,
            String search,
            StatusEnum status,
            String role,
            String sortBy,
            String sortDir
    ) {
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy
        );

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        boolean hasSearch = search != null && !search.isBlank();
        boolean hasStatus = status != null;
        boolean hasRole = role != null && !role.isBlank();

        Page<Profile> profiles;

        if (hasSearch && hasStatus && hasRole) {
            profiles = profileRepository.searchByRoleAndStatus(
                    search, status, role, sortedPageable
            );
        } else if (hasSearch && hasStatus) {
            profiles = profileRepository.searchByStatus(
                    search, status, sortedPageable
            );
        } else if (hasSearch && hasRole) {
            profiles = profileRepository.searchByRole(
                    search, role, sortedPageable
            );
        } else if (hasStatus && hasRole) {
            profiles = profileRepository.findByStatus(status, sortedPageable);
            profiles = new PageImpl<>(
                    profiles.getContent().stream()
                            .filter(profile -> role.equals(profile.getRoleId()))
                            .collect(Collectors.toList()),
                    pageable,
                    profiles.getTotalElements()
            );
        } else if (hasStatus) {
            profiles = profileRepository.findByStatus(status, sortedPageable);
        } else if (hasRole) {
            profiles = profileRepository.searchByRole("", role, sortedPageable);
        } else if (hasSearch) {
            profiles = profileRepository.search(search, sortedPageable);
        } else {
            profiles = profileRepository.findAll(sortedPageable);
        }

        return profiles.map(this::mapToUserResponse);
    }

    @Override
    public UserResponse getUserById(String id) throws GenericException {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        return mapToUserResponse(profile);
    }

    @Override
    public UserResponse updateUserStatus(String id, StatusUpdateRequest request) throws GenericException {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        
        profile.setStatus(request.getStatus());
        profileRepository.save(profile);
        
        return mapToUserResponse(profile);
    }

    @Override
    public UserResponse updateUserRole(String id, RoleUpdateRequest request) throws GenericException {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        
        profile.setRoleId(request.getRole());
        profileRepository.save(profile);
        
        return mapToUserResponse(profile);
    }

    @Override
    public UserResponse toggleUserVerification(String id) throws GenericException {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));

        if (profile.getEmailVerified() == VerificationStatusEnum.VERIFIED) {
            profile.setEmailVerified(VerificationStatusEnum.PENDING);
        } else {
            profile.setEmailVerified(VerificationStatusEnum.VERIFIED);
        }
        if (profile.getPhoneVerified() == VerificationStatusEnum.VERIFIED) {
            profile.setPhoneVerified(VerificationStatusEnum.PENDING);
        } else {
            profile.setPhoneVerified(VerificationStatusEnum.VERIFIED);
        }
        
        profile.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(profile);
        
        return mapToUserResponse(profile);
    }

    private UserResponse mapToUserResponse(Profile profile){
        UserResponse user = UserResponse.builder()
                .id(profile.getId())
                .fullName(profile.getFullName())
                .userName(profile.getUserName())
                .email(profile.getEmail())
                .roleId(profile.getRoleId())
                .roleName(getRoleNameById(profile.getRoleId()))
                .status(profile.getStatus())
                .emailVerified(profile.getEmailVerified())
                .phoneVerified(profile.getPhoneVerified())
                .profileImageUrl(profile.getProfileImageUrl())
                .build();
        helper.setAudit(profile, user);
        return user;
    }

    private String getRoleNameById(String roleId) {
        if (roleId == null || roleId.isBlank()) {
            return null;
        }
        return roleRepository.findById(roleId)
                .map(Role::getName)
                .orElse(null);
    }
}
