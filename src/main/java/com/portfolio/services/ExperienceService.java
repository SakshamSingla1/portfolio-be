package com.portfolio.services;

import com.portfolio.dtos.ExperienceRequest;
import com.portfolio.dtos.ExperienceResponse;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExperienceService {
    ExperienceResponse create(ExperienceRequest request) throws GenericException;
    ExperienceResponse update(String id,ExperienceRequest request) throws GenericException;
    ExperienceResponse getById(String id) throws GenericException;
    String delete(String id) throws GenericException;
    Page<ExperienceResponse> getByProfile(String profileId, String search , String sortDir, String sortBy, Pageable pageable);
    List<ExperienceResponse> getByProfile(String profileId);
}
