package com.portfolio.services;

import com.portfolio.dtos.ProfileTheme.ProfileThemeRequest;
import com.portfolio.dtos.ProfileTheme.ProfileThemeResponse;
import com.portfolio.exceptions.GenericException;

import java.util.List;

public interface ProfileThemeService {
    ProfileThemeResponse getThemeByProfileId(String profileId) throws GenericException;
    ProfileThemeResponse setThemeForProfile(String profileId, ProfileThemeRequest request) throws GenericException;
    List<ProfileThemeResponse> getProfilesByThemeId(String themeId) throws GenericException;
    long countProfilesByThemeId(String themeId);
}