package com.portfolio.services;

import com.portfolio.dtos.ProfileTemplate.ProfileTemplateRequest;
import com.portfolio.dtos.ProfileTemplate.ProfileTemplateResponse;
import com.portfolio.exceptions.GenericException;

public interface ProfileTemplateService {
    ProfileTemplateResponse getTemplateByProfileId(Long profileId) throws GenericException;
    ProfileTemplateResponse setTemplateForProfile(Long profileId, ProfileTemplateRequest request) throws GenericException;
    void resetTemplateForProfile(Long profileId) throws GenericException;
    long countProfilesByTemplateKey(String templateKey);
}
