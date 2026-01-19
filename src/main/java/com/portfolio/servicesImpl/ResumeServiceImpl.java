package com.portfolio.servicesImpl;

import com.portfolio.dtos.Resume.ResumeUploadResponseDTO;
import com.portfolio.entities.Resume;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ResumeRepository;
import com.portfolio.services.CloudinaryService;
import com.portfolio.services.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public ResumeUploadResponseDTO uploadResume(String profileId, MultipartFile file) throws IOException {
        Map uploadResult = cloudinaryService.uploadFile(file);
        resumeRepository.findByProfileIdAndStatus(profileId, StatusEnum.ACTIVE)
                .ifPresent(existing -> {
                    existing.setStatus(StatusEnum.INACTIVE);
                    resumeRepository.save(existing);
                });
        Resume resume = Resume.builder()
                .profileId(profileId)
                .fileName(file.getOriginalFilename())
                .fileUrl(uploadResult.get("secure_url").toString())
                .publicId(uploadResult.get("public_id").toString())
                .status(StatusEnum.ACTIVE)
                .uploadedAt(LocalDateTime.now())
                .build();
        Resume saved = resumeRepository.save(resume);
        return ResumeUploadResponseDTO.builder()
                .id(saved.getId())
                .fileName(saved.getFileName())
                .fileUrl(saved.getFileUrl())
                .status(saved.getStatus())
                .uploadedAt(saved.getUploadedAt())
                .build();
    }

    @Override
    public List<ResumeUploadResponseDTO> getResumes(String profileId) {
        return resumeRepository.findByProfileIdAndStatusNotOrderByUploadedAtDesc(profileId,StatusEnum.DELETED)
                .stream()
                .map(r -> ResumeUploadResponseDTO.builder()
                        .id(r.getId())
                        .fileUrl(r.getFileUrl())
                        .fileName(r.getFileName())
                        .status(r.getStatus())
                        .uploadedAt(r.getUploadedAt())
                        .build())
                .toList();
    }

    @Override
    public void activateResume(String profileId, String resumeId) throws GenericException {
        resumeRepository.findByProfileIdAndStatus(profileId, StatusEnum.ACTIVE)
                .ifPresent(existing -> {
                    existing.setStatus(StatusEnum.INACTIVE);
                    resumeRepository.save(existing);
                });
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.RESUME_NOT_FOUND,"Resume not found"));
        resume.setStatus(StatusEnum.ACTIVE);
        resumeRepository.save(resume);
    }

    @Override
    public void deleteResume(String resumeId) throws GenericException {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new GenericException(
                        ExceptionCodeEnum.RESUME_NOT_FOUND,
                        "Resume not found"
                ));
        try {
            cloudinaryService.deleteFile(resume.getPublicId());
        } catch (Exception ex) {
            // log.warn("Cloudinary delete failed for resumeId={}", resumeId, ex);
        }
        resume.setStatus(StatusEnum.DELETED);
        resumeRepository.save(resume);
    }

}
