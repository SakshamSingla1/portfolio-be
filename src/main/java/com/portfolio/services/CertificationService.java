package com.portfolio.services;

import com.portfolio.dtos.Certifications.CertificationRequestDTO;
import com.portfolio.dtos.Certifications.CertificationResponseDTO;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CertificationService {
    CertificationResponseDTO createCertification(CertificationRequestDTO certificationRequestDTO) throws GenericException;
    CertificationResponseDTO updateCertification(Long id, CertificationRequestDTO certificationDTO) throws GenericException;
    CertificationResponseDTO getCertificationById(Long id) throws GenericException;
    Page<CertificationResponseDTO> getByProfile(Long profileId, String search, Pageable pageable);
    Void deleteById(Long id) throws GenericException;
    ImageUploadResponse uploadCredentialImage(Long id,MultipartFile file) throws GenericException, IOException;
    List<CertificationResponseDTO> getByProfile(Long profileId);
}
