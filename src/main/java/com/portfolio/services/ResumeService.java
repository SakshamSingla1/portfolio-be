package com.portfolio.services;

import com.portfolio.dtos.Resume.ResumeUploadResponseDTO;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResumeService {
    ResumeUploadResponseDTO uploadResume(String profileId, MultipartFile file) throws IOException;
    Page<ResumeUploadResponseDTO> getByProfile(String profileId, StatusEnum status, Pageable pageable, String search, String sortDir, String sortBy);
    void activateResume(String profileId, String resumeId) throws GenericException;
    void deleteResume(String resumeId) throws GenericException;
}
