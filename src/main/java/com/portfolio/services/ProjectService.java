package com.portfolio.services;

import com.portfolio.dtos.ImageUploadResponse;
import com.portfolio.dtos.ProjectRequest;
import com.portfolio.dtos.ProjectResponse;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProjectService {

    ProjectResponse create(ProjectRequest request) throws GenericException;

    ProjectResponse update(String id, ProjectRequest request) throws GenericException;

    ProjectResponse getById(String id) throws GenericException;

    ImageUploadResponse uploadProjectImage(String profileId, MultipartFile file) throws IOException, GenericException;

    String delete(String id) throws GenericException;

    Page<ProjectResponse> getByProfile(String profileId, Pageable pageable, String search, String sortDir, String sortBy);

    List<ProjectResponse> getByProfile(String profileId);
}
