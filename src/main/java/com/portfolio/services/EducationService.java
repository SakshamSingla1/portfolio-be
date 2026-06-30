package com.portfolio.services;

import com.portfolio.dtos.Education.EducationRequest;
import com.portfolio.dtos.Education.EducationResponse;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EducationService {
    EducationResponse createEducation(EducationRequest request) throws GenericException;
    EducationResponse updateEducation(Long id, EducationRequest request) throws GenericException;
    EducationResponse findById(Long id,Long profileId) throws GenericException;
    String delete(Long id,Long profileId) throws GenericException;
    Page<EducationResponse> getByProfile(Long profileId, String search, Pageable pageable);
    List<EducationResponse> getByProfile(Long profileId);
}
