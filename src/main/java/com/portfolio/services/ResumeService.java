package com.portfolio.services;

import com.portfolio.dtos.Resume.ResumeUploadResponseDTO;
import com.portfolio.exceptions.GenericException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ResumeService {
    ResumeUploadResponseDTO uploadResume(String profileId, MultipartFile file) throws IOException;
    List<ResumeUploadResponseDTO> getResumes(String profileId);
    void activateResume(String profileId, String resumeId) throws GenericException;
    void deleteResume(String resumeId) throws GenericException;
}
