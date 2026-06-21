package com.portfolio.services;

import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.dtos.Project.ProjectRequest;
import com.portfolio.dtos.Project.ProjectResponse;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProjectService {

    ProjectResponse create(ProjectRequest request) throws GenericException;

    ProjectResponse update(Long id, ProjectRequest request) throws GenericException;

    ProjectResponse getById(Long id) throws GenericException;

    ImageUploadResponse uploadProjectImage(Long profileId, MultipartFile file) throws IOException, GenericException;

    String delete(Long id) throws GenericException;

    Page<ProjectResponse> getByProfile(Long profileId, Pageable pageable, String search, String sortDir, String sortBy);

    List<ProjectResponse> getByProfile(Long profileId);
}
