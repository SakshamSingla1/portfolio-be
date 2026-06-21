package com.portfolio.services;

import com.portfolio.dtos.Experience.ExperienceRequest;
import com.portfolio.dtos.Experience.ExperienceResponse;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExperienceService {
    ExperienceResponse create(ExperienceRequest request) throws GenericException;
    ExperienceResponse update(Long id,ExperienceRequest request) throws GenericException;
    ExperienceResponse getById(Long id) throws GenericException;
    String delete(Long id) throws GenericException;
    Page<ExperienceResponse> getByProfile(Long profileId, String search , String sortDir, String sortBy, Pageable pageable);
    List<ExperienceResponse> getByProfile(Long profileId);
}
