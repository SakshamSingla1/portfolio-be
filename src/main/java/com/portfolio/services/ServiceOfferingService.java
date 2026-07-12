package com.portfolio.services;

import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.dtos.Services.ServiceRequest;
import com.portfolio.dtos.Services.ServiceResponse;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ServiceOfferingService {
    ServiceResponse create(ServiceRequest req) throws GenericException;
    ServiceResponse update(Long id, ServiceRequest req) throws GenericException;
    ServiceResponse getById(Long id) throws GenericException;
    Page<ServiceResponse> getAll(Long profileId, String search, Pageable pageable);
    Void delete(Long id) throws GenericException;
    ImageUploadResponse uploadBanner(Long profileId, MultipartFile file) throws GenericException, IOException;
    List<ServiceResponse> getByProfile(Long profileId);
}
