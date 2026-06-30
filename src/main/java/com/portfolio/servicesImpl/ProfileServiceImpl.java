package com.portfolio.servicesImpl;

import com.portfolio.dao.file.FileAssetDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dao.role.RoleDao;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileDao profileDao;
    private final FileService fileService;
    private final FileAssetDao fileAssetDao;
    private final Helper helper;
    private final RoleDao roleDao;

    @Override
    public ProfileResponse get(Long id) throws GenericException {
        Profile profile = profileDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        return mapToResponse(profile);
    }

    @Override
    public ProfileResponse update(Long id, ProfileRequest req) throws GenericException, IOException {

        Profile existing = profileDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        profileDao.findByEmail(req.getEmail())
                .ifPresent(owner -> {
                    if (!owner.getId().equals(id)) {
                        throw new RuntimeException(
                                new GenericException(ExceptionCodeEnum.DUPLICATE_PROFILE, "Email already in use"));
                    }
                });
        profileDao.findByPhone(req.getPhone())
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
        Profile updated = profileDao.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public ImageUploadResponse uploadProfileImage(
            Long profileId,
            MultipartFile file) throws IOException, GenericException {
        profileDao.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        fileAssetDao.findByResourceIdAndResourceTypeAndIsPrimaryTrue(profileId.intValue(), ResourceTypeEnum.PROFILE)
                .ifPresent(existing -> {
                    try { fileService.delete(existing.getId()); } catch (Exception ignored) {}
                });
        FileUploadRequest uploadReq = new FileUploadRequest();
        uploadReq.setResourceId(profileId.intValue());
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
        profileDao.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        List<FileAsset> existingList = fileAssetDao.findByResourceIdAndResourceTypeOrderBySortOrderAsc(profileId.intValue(), ResourceTypeEnum.PROFILE);
        for (FileAsset asset : existingList) {
            if ("ABOUT_ME_IMAGE".equals(asset.getMetaData())) {
                try { fileService.delete(asset.getId()); } catch (Exception ignored) {}
            }
        }
        FileUploadRequest uploadReq = new FileUploadRequest();
        uploadReq.setResourceId(profileId.intValue());
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
        profileDao.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        fileAssetDao.findByResourceIdAndResourceTypeAndIsPrimaryTrue(profileId.intValue(), ResourceTypeEnum.LOGO)
                .ifPresent(existing -> {
                    try { fileService.delete(existing.getId()); } catch (Exception ignored) {}
                });
        FileUploadRequest uploadReq = new FileUploadRequest();
        uploadReq.setResourceId(profileId.intValue());
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

        List<FileAsset> profileAssets = fileAssetDao.findByResourceIdAndResourceTypeOrderBySortOrderAsc(profile.getId().intValue(), ResourceTypeEnum.PROFILE);
        for (FileAsset asset : profileAssets) {
            if (asset.isPrimary() || "PROFILE_IMAGE".equals(asset.getMetaData())) {
                profileImageUrl = asset.getPath();
                profileImagePublicId = asset.getPublicId();
            } else if ("ABOUT_ME_IMAGE".equals(asset.getMetaData())) {
                aboutMeImageUrl = asset.getPath();
                aboutMeImagePublicId = asset.getPublicId();
            }
        }

        Optional<FileAsset> logoAsset = fileAssetDao.findByResourceIdAndResourceTypeAndIsPrimaryTrue(profile.getId().intValue(), ResourceTypeEnum.LOGO);
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
            String status,
            String roleIdString) {
                List<Long> roleIds = helper.parseIds(roleIdString);
                List<StatusEnum> statusList = helper.parseStatuses(status);
        return profileDao.findByCriteria(search, statusList, roleIds, pageable);
    }

    @Override
    public UserResponse getUserById(Long id) throws GenericException {
        Profile profile = profileDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        return mapToUserResponse(profile);
    }

    @Override
    public UserResponse updateUserStatus(Long id, StatusUpdateRequest request) throws GenericException {
        Profile profile = profileDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));

        profile.setStatus(request.getStatus());
        profileDao.save(profile);

        return mapToUserResponse(profile);
    }

    @Override
    public UserResponse updateUserRole(Long id, RoleUpdateRequest request) throws GenericException {
        Profile profile = profileDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));

        profile.setRoleId(Long.parseLong(request.getRole()));
        profileDao.save(profile);

        return mapToUserResponse(profile);
    }

    @Override
    public UserResponse toggleUserVerification(Long id) throws GenericException {
        Profile profile = profileDao.findById(id)
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
        profileDao.save(profile);

        return mapToUserResponse(profile);
    }

    private UserResponse mapToUserResponse(Profile profile) {
        String profileImageUrl = null;
        List<FileAsset> profileAssets = fileAssetDao.findByResourceIdAndResourceTypeOrderBySortOrderAsc(profile.getId().intValue(), ResourceTypeEnum.PROFILE);
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
        return user;
    }

    private String getRoleNameById(Long roleId) {
        if (roleId == null) {
            return null;
        }
        return roleDao.findById(roleId)
                .map(Role::getName)
                .orElse(null);
    }
}
