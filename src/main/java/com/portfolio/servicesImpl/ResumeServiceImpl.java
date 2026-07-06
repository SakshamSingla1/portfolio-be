package com.portfolio.servicesImpl;

import com.portfolio.dao.resume.ResumeDao;
import com.portfolio.dtos.Resume.ResumeUploadResponseDTO;
import com.portfolio.entities.Resume;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.dtos.File.FileUploadRequest;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.FileService;
import com.portfolio.services.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeDao resumeDao;
    private final FileService fileService;

    @Override
    @Transactional
    public ResumeUploadResponseDTO uploadResume(Long profileId, MultipartFile file) throws IOException {
        resumeDao.findByProfileIdAndStatus(profileId, StatusEnum.ACTIVE)
                .ifPresent(existing -> {
                    existing.setStatus(StatusEnum.INACTIVE);
                    resumeDao.save(existing);
                });
        Resume resume = Resume.builder()
                .profileId(profileId)
                .status(StatusEnum.ACTIVE)
                .build();
        Resume saved = resumeDao.save(resume);

        FileUploadRequest uploadReq = new FileUploadRequest();
        uploadReq.setResourceId(saved.getId());
        uploadReq.setResourceType(ResourceTypeEnum.RESUME);
        uploadReq.setPrimary(true);
        uploadReq.setMetaData(file.getOriginalFilename());
        try {
            fileService.upload(file, uploadReq);
        } catch (Exception e) {
            throw new IOException("Failed to upload resume file: " + e.getMessage(), e);
        }
        return resumeDao.findDTOById(saved.getId())
                .orElse(ResumeUploadResponseDTO.builder().id(saved.getId()).status(saved.getStatus()).build());
    }

    @Override
    public Page<ResumeUploadResponseDTO> getByProfile(Long profileId, StatusEnum status, Pageable pageable, String search, String sortDir, String sortBy) {
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return resumeDao.findByCriteria(profileId, status, search, sortedPageable);
    }

    @Override
    @Transactional
    public void activateResume(Long profileId, Long resumeId) throws GenericException {
        resumeDao.findByProfileIdAndStatus(profileId, StatusEnum.ACTIVE)
                .ifPresent(existing -> {
                    existing.setStatus(StatusEnum.INACTIVE);
                    resumeDao.save(existing);
                });
        Resume resume = resumeDao.findById(resumeId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.RESUME_NOT_FOUND, "Resume not found"));
        resume.setStatus(StatusEnum.ACTIVE);
        resume.setUpdatedAt(LocalDateTime.now());
        resumeDao.save(resume);
    }

    @Override
    public void deleteResume(Long resumeId) throws GenericException {
        Resume resume = resumeDao.findById(resumeId)
                .orElseThrow(() -> new GenericException(
                        ExceptionCodeEnum.RESUME_NOT_FOUND,
                        "Resume not found"
                ));
        try {
            fileService.deleteByResource(resumeId, ResourceTypeEnum.RESUME.name());
        } catch (Exception ignored) {}
        resume.setStatus(StatusEnum.DELETED);
        resume.setUpdatedAt(LocalDateTime.now());
        resumeDao.save(resume);
    }
}
