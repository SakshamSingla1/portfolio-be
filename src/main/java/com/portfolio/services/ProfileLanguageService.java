package com.portfolio.services;

import com.portfolio.dtos.Language.ProfileLanguageRequest;
import com.portfolio.dtos.Language.ProfileLanguageResponse;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProfileLanguageService {
    ProfileLanguageResponse create(ProfileLanguageRequest req) throws GenericException;
    ProfileLanguageResponse update(Long id, ProfileLanguageRequest req) throws GenericException;
    ProfileLanguageResponse getById(Long id) throws GenericException;
    Page<ProfileLanguageResponse> getAll(Long profileId, String search, Pageable pageable);
    Void delete(Long id) throws GenericException;
    List<ProfileLanguageResponse> getByProfile(Long profileId);
}
