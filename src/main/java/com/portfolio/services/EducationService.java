package com.portfolio.services;

import com.portfolio.dtos.EducationRequest;
import com.portfolio.dtos.EducationResponse;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EducationService {
    EducationResponse createEducation(EducationRequest request) throws GenericException;
    EducationResponse updateEducation(String id, EducationRequest request) throws GenericException;
    EducationResponse findById(String id,String profileId) throws GenericException;
    String delete(String id,String profileId) throws GenericException;
    Page<EducationResponse> getByProfile(String profileId, String search, String sortDir, String sortBy, Pageable pageable);
    List<EducationResponse> getByProfile(String profileId);
}
