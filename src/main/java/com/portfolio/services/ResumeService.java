package com.portfolio.services;

import com.portfolio.dtos.Resume.ResumeUploadResponseDTO;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResumeService {
    ResumeUploadResponseDTO uploadResume(Long profileId, MultipartFile file) throws IOException;
    Page<ResumeUploadResponseDTO> getByProfile(Long profileId, StatusEnum status, Pageable pageable, String search, String sortDir, String sortBy);
    void activateResume(Long profileId, Long resumeId) throws GenericException;
    void deleteResume(Long resumeId) throws GenericException;
}
