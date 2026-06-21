package com.portfolio.services;

import com.portfolio.dtos.ProfileTheme.ProfileThemeRequest;
import com.portfolio.dtos.ProfileTheme.ProfileThemeResponse;
import com.portfolio.exceptions.GenericException;

import java.util.List;

public interface ProfileThemeService {
    ProfileThemeResponse getThemeByProfileId(Long profileId) throws GenericException;
    ProfileThemeResponse setThemeForProfile(Long profileId, ProfileThemeRequest request) throws GenericException;
    List<ProfileThemeResponse> getProfilesByThemeId(Long themeId) throws GenericException;
    long countProfilesByThemeId(Long themeId);
}