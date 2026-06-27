package com.portfolio.servicesImpl;

import com.portfolio.dtos.Admin.RoleUpdateRequest;
import com.portfolio.dtos.Admin.StatusUpdateRequest;
import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.dtos.File.FileUploadRequest;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.dtos.Profile.ProfileRequest;
import com.portfolio.dtos.Profile.ProfileResponse;
import com.portfolio.dtos.User.UserResponse;
import com.portfolio.entities.FileAsset;
import com.portfolio.entities.Profile;
import com.portfolio.entities.Role;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.VerificationStatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.FileAssetRepository;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.repositories.RoleRepository;
import com.portfolio.services.FileService;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final FileService fileService;
    private final FileAssetRepository fileAssetRepository;
    private final Helper helper;
    private final RoleRepository roleRepository;

    @Override
    public ProfileResponse get(Long id) throws GenericException {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        return mapToResponse(profile);
    }

    @Override
    public ProfileResponse update(Long id, ProfileRequest req) throws GenericException, IOException {

        Profile existing = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        profileRepository.findByEmail(req.getEmail())
                .ifPresent(owner -> {
                    if (!owner.getId().equals(id)) {
                        throw new RuntimeException(
                                new GenericException(ExceptionCodeEnum.DUPLICATE_PROFILE, "Email already in use"));
                    }
                });
        profileRepository.findByPhone(req.getPhone())
                .ifPresent(owner -> {
                    if (!owner.getId().equals(id)) {
                        throw new RuntimeException(
                                new GenericException(ExceptionCodeEnum.DUPLICATE_PROFILE, "Phone already in use"));
                    }
                });
        existing.setFullName(req.getFullName());
        existing.setTitle(req.getTitle());
        existing.setAboutMe(req.getAboutMe());
        existing.setEmail(req.getEmail());
        existing.setPhone(req.getPhone());
        existing.setLocation(req.getLocation());
        existing.setUpdatedAt(LocalDateTime.now());
        Profile updated = profileRepository.save(existing);
        return mapToResponse(updated);
    }
 
    @Override
    public ImageUploadResponse uploadProfileImage(
            Long profileId,
            MultipartFile file) throws IOException, GenericException {
        profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        fileAssetRepository.findByResourceIdAndResourceTypeAndIsPrimaryTrue(String.valueOf(profileId), ResourceTypeEnum.PROFILE)
                .ifPresent(existing -> {
                    try { fileService.delete(existing.getId()); } catch (Exception ignored) {}
                });
        FileUploadRequest uploadReq = new FileUploadRequest();
        uploadReq.setResourceId(String.valueOf(profileId));
        uploadReq.setResourceType(ResourceTypeEnum.PROFILE);
        uploadReq.setPrimary(true);
        uploadReq.setMetaData("PROFILE_IMAGE");
        try {
            FileAssetDTO asset = fileService.upload(file, uploadReq);
            return new ImageUploadResponse(asset.getPath(), asset.getPublicId());
        } catch (Exception e) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Failed to upload profile image: " + e.getMessage());
        }
    }
 
    @Override
    public ImageUploadResponse uploadAboutMeImage(
            Long profileId,
            MultipartFile file) throws IOException, GenericException {
        profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        List<FileAsset> existingList = fileAssetRepository.findByResourceIdAndResourceTypeOrderBySortOrderAsc(String.valueOf(profileId), ResourceTypeEnum.PROFILE);
        for (FileAsset asset : existingList) {
            if ("ABOUT_ME_IMAGE".equals(asset.getMetaData())) {
                try { fileService.delete(asset.getId()); } catch (Exception ignored) {}
            }
        }
        FileUploadRequest uploadReq = new FileUploadRequest();
        uploadReq.setResourceId(String.valueOf(profileId));
        uploadReq.setResourceType(ResourceTypeEnum.PROFILE);
        uploadReq.setPrimary(false);
        uploadReq.setMetaData("ABOUT_ME_IMAGE");
        try {
            FileAssetDTO asset = fileService.upload(file, uploadReq);
            return new ImageUploadResponse(asset.getPath(), asset.getPublicId());
        } catch (Exception e) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Failed to upload about me image: " + e.getMessage());
        }
    }
 
    @Override
    public ImageUploadResponse uploadLogoImage(
            Long profileId,
            MultipartFile file) throws IOException, GenericException {
        profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        fileAssetRepository.findByResourceIdAndResourceTypeAndIsPrimaryTrue(String.valueOf(profileId), ResourceTypeEnum.LOGO)
                .ifPresent(existing -> {
                    try { fileService.delete(existing.getId()); } catch (Exception ignored) {}
                });
        FileUploadRequest uploadReq = new FileUploadRequest();
        uploadReq.setResourceId(String.valueOf(profileId));
        uploadReq.setResourceType(ResourceTypeEnum.LOGO);
        uploadReq.setPrimary(true);
        try {
            FileAssetDTO asset = fileService.upload(file, uploadReq);
            return new ImageUploadResponse(asset.getPath(), asset.getPublicId());
        } catch (Exception e) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Failed to upload logo: " + e.getMessage());
        }
    }
 
    private ProfileResponse mapToResponse(Profile profile) {
        String profileImageUrl = null;
        String profileImagePublicId = null;
        String aboutMeImageUrl = null;
        String aboutMeImagePublicId = null;
        String logoUrl = null;
        String logoPublicId = null;
 
        List<FileAsset> profileAssets = fileAssetRepository.findByResourceIdAndResourceTypeOrderBySortOrderAsc(String.valueOf(profile.getId()), ResourceTypeEnum.PROFILE);
        for (FileAsset asset : profileAssets) {
            if (asset.isPrimary() || "PROFILE_IMAGE".equals(asset.getMetaData())) {
                profileImageUrl = asset.getPath();
                profileImagePublicId = asset.getPublicId();
            } else if ("ABOUT_ME_IMAGE".equals(asset.getMetaData())) {
                aboutMeImageUrl = asset.getPath();
                aboutMeImagePublicId = asset.getPublicId();
            }
        }
 
        Optional<FileAsset> logoAsset = fileAssetRepository.findByResourceIdAndResourceTypeAndIsPrimaryTrue(String.valueOf(profile.getId()), ResourceTypeEnum.LOGO);
        if (logoAsset.isPresent()) {
            logoUrl = logoAsset.get().getPath();
            logoPublicId = logoAsset.get().getPublicId();
        }
 
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
                .profileImageUrl(profileImageUrl)
                .profileImagePublicId(profileImagePublicId)
                .aboutMeImageUrl(aboutMeImageUrl)
                .aboutMeImagePublicId(aboutMeImagePublicId)
                .logoUrl(logoUrl)
                .logoPublicId(logoPublicId)
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
            String sortDir) {
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy);

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort);

        boolean hasSearch = search != null && !search.isBlank();
        boolean hasStatus = status != null;
        boolean hasRole = role != null && !role.isBlank();
        Long numericRole = hasRole ? Long.valueOf(role) : null;

        Page<Profile> profiles;

        if (hasSearch && hasStatus && hasRole) {
            profiles = profileRepository.searchByRoleAndStatus(
                    search, status, numericRole, sortedPageable);
        } else if (hasSearch && hasStatus) {
            profiles = profileRepository.searchByStatus(
                    search, status, sortedPageable);
        } else if (hasSearch && hasRole) {
            profiles = profileRepository.searchByRole(
                    search, numericRole, sortedPageable);
        } else if (hasStatus && hasRole) {
            profiles = profileRepository.findByStatus(status, sortedPageable);
            profiles = new PageImpl<>(
                    profiles.getContent().stream()
                            .filter(profile -> numericRole.equals(profile.getRoleId()))
                            .collect(Collectors.toList()),
                    pageable,
                    profiles.getTotalElements());
        } else if (hasStatus) {
            profiles = profileRepository.findByStatus(status, sortedPageable);
        } else if (hasRole) {
            profiles = profileRepository.searchByRole("", numericRole, sortedPageable);
        } else if (hasSearch) {
            profiles = profileRepository.search(search, sortedPageable);
        } else {
            profiles = profileRepository.findAll(sortedPageable);
        }

        return profiles.map(this::mapToUserResponse);
    }

    @Override
    public UserResponse getUserById(Long id) throws GenericException {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        return mapToUserResponse(profile);
    }

    @Override
    public UserResponse updateUserStatus(Long id, StatusUpdateRequest request) throws GenericException {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));

        profile.setStatus(request.getStatus());
        profileRepository.save(profile);

        return mapToUserResponse(profile);
    }

    @Override
    public UserResponse updateUserRole(Long id, RoleUpdateRequest request) throws GenericException {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));

        profile.setRoleId(Long.parseLong(request.getRole()));
        profileRepository.save(profile);

        return mapToUserResponse(profile);
    }

    @Override
    public UserResponse toggleUserVerification(Long id) throws GenericException {
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

    private UserResponse mapToUserResponse(Profile profile) {
        String profileImageUrl = null;
        List<FileAsset> profileAssets = fileAssetRepository.findByResourceIdAndResourceTypeOrderBySortOrderAsc(String.valueOf(profile.getId()), ResourceTypeEnum.PROFILE);
        for (FileAsset asset : profileAssets) {
            if (asset.isPrimary() || "PROFILE_IMAGE".equals(asset.getMetaData())) {
                profileImageUrl = asset.getPath();
                break;
            }
        }

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
                .profileImageUrl(profileImageUrl)
                .build();
        helper.setAudit(profile, user);
        return user;
    }

    private String getRoleNameById(Long roleId) {
        if (roleId == null) {
            return null;
        }
        return roleRepository.findById(roleId)
                .map(Role::getName)
                .orElse(null);
    }
}
