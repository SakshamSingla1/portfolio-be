package com.portfolio.servicesImpl;

import com.portfolio.dtos.ProfileTheme.ProfileThemeRequest;
import com.portfolio.dtos.ProfileTheme.ProfileThemeResponse;
import com.portfolio.entities.ColorTheme;
import com.portfolio.entities.Profile;
import com.portfolio.entities.ProfileThemeMapping;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ColorThemeRepository;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.repositories.ProfileThemeMappingRepository;
import com.portfolio.services.ProfileThemeService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileThemeServiceImpl implements ProfileThemeService {

    private final ProfileThemeMappingRepository mappingRepository;
    private final ProfileRepository profileRepository;
    private final ColorThemeRepository themeRepository;
    private final Helper helper;

    @Override
    public ProfileThemeResponse getThemeByProfileId(String profileId) throws GenericException {
        ProfileThemeMapping mapping = mappingRepository.findByProfileId(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.DATA_NOT_FOUND, "Theme mapping not found for profile: " + profileId));
        
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found: " + profileId));
        
        ColorTheme theme = themeRepository.findById(mapping.getThemeId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND, "Theme not found: " + mapping.getThemeId()));
        
        ProfileThemeResponse response = mapToResponse(mapping, profile, theme);
        helper.setAudit(mapping, response);
        return response;
    }

    @Override
    public ProfileThemeResponse setThemeForProfile(String profileId, ProfileThemeRequest request) throws GenericException {
        profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found: " + profileId));
        
        themeRepository.findById(request.getThemeId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND, "Theme not found: " + request.getThemeId()));

        ProfileThemeMapping mapping = mappingRepository.findByProfileId(profileId)
                .orElse(ProfileThemeMapping.builder()
                        .profileId(profileId)
                        .build());

        mapping.setThemeId(request.getThemeId());
        mapping.setUpdatedAt(LocalDateTime.now());
        mappingRepository.save(mapping);

        return getThemeByProfileId(profileId);
    }

    @Override
    public List<ProfileThemeResponse> getProfilesByThemeId(String themeId) throws GenericException {
        List<ProfileThemeMapping> mappings = mappingRepository.findAllByThemeId(themeId);
        ColorTheme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND, "Theme not found: " + themeId));

        return mappings.stream()
                .map(mapping -> {
                    Profile profile = profileRepository.findById(mapping.getProfileId()).orElse(null);
                    ProfileThemeResponse response = mapToResponse(mapping, profile, theme);
                    helper.setAudit(mapping, response);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public long countProfilesByThemeId(String themeId) {
        return mappingRepository.countByThemeId(themeId);
    }

    private ProfileThemeResponse mapToResponse(ProfileThemeMapping mapping, Profile profile, ColorTheme theme) {
        return ProfileThemeResponse.builder()
                .id(mapping.getId())
                .profileId(mapping.getProfileId())
                .username(profile != null ? profile.getUserName() : null)
                .themeId(mapping.getThemeId())
                .themeName(theme != null ? theme.getThemeName() : null)
                .build();
    }
}
