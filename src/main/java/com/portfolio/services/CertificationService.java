package com.portfolio.services;

import com.portfolio.dtos.Certifications.CertificationRequestDTO;
import com.portfolio.dtos.Certifications.CertificationResponseDTO;
import com.portfolio.dtos.ImageUploadResponse;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CertificationService {
    CertificationResponseDTO createCertification(CertificationRequestDTO certificationRequestDTO) throws GenericException;
    CertificationResponseDTO updateCertification(String id, CertificationRequestDTO certificationDTO) throws GenericException;
    CertificationResponseDTO getCertificationById(String id) throws GenericException;
    Page<CertificationResponseDTO> getByProfile(String profileId, String search, String sortDir, String sortBy, Pageable pageable);
    Void deleteById(String id) throws GenericException;
    ImageUploadResponse uploadCredentialImage(String id,MultipartFile file) throws GenericException, IOException;
    List<CertificationResponseDTO> getByProfile(String profileId);
}
