package com.portfolio.servicesImpl;

import com.portfolio.dao.color_theme.ColorThemeDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dao.profile_theme.ProfileThemeMappingDao;
import com.portfolio.dtos.ProfileTheme.ProfileThemeRequest;
import com.portfolio.dtos.ProfileTheme.ProfileThemeResponse;
import com.portfolio.entities.ColorTheme;
import com.portfolio.entities.Profile;
import com.portfolio.entities.ProfileThemeMapping;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
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

    private final ProfileThemeMappingDao profileThemeMappingDao;
    private final ProfileDao profileDao;
    private final ColorThemeDao colorThemeDao;
    private final Helper helper;

    @Override
    public ProfileThemeResponse getThemeByProfileId(Long profileId) throws GenericException {
        ProfileThemeMapping mapping = profileThemeMappingDao.findByProfileId(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.DATA_NOT_FOUND, "Theme mapping not found for profile: " + profileId));

        Profile profile = profileDao.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found: " + profileId));

        ColorTheme theme = colorThemeDao.findById(mapping.getThemeId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND, "Theme not found: " + mapping.getThemeId()));

        ProfileThemeResponse response = mapToResponse(mapping, profile, theme);
        helper.setAudit(mapping, response);
        return response;
    }

    @Override
    public ProfileThemeResponse setThemeForProfile(Long profileId, ProfileThemeRequest request) throws GenericException {
        profileDao.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found: " + profileId));

        colorThemeDao.findById(request.getThemeId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND, "Theme not found: " + request.getThemeId()));

        ProfileThemeMapping mapping = profileThemeMappingDao.findByProfileId(profileId)
                .orElse(ProfileThemeMapping.builder()
                        .profileId(profileId)
                        .build());

        mapping.setThemeId(request.getThemeId());
        mapping.setUpdatedAt(LocalDateTime.now());
        profileThemeMappingDao.save(mapping);

        return getThemeByProfileId(profileId);
    }

    @Override
    public List<ProfileThemeResponse> getProfilesByThemeId(Long themeId) throws GenericException {
        List<ProfileThemeMapping> mappings = profileThemeMappingDao.findAllByThemeId(themeId);
        ColorTheme theme = colorThemeDao.findById(themeId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND, "Theme not found: " + themeId));

        return mappings.stream()
                .map(mapping -> {
                    Profile profile = profileDao.findById(mapping.getProfileId()).orElse(null);
                    ProfileThemeResponse response = mapToResponse(mapping, profile, theme);
                    helper.setAudit(mapping, response);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public long countProfilesByThemeId(Long themeId) {
        return profileThemeMappingDao.countByThemeId(themeId);
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
