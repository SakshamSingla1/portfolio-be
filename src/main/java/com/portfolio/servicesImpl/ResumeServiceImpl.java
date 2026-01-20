package com.portfolio.servicesImpl;

import com.portfolio.dtos.NavLinks.NavLinkResponseDTO;
import com.portfolio.dtos.ProjectResponse;
import com.portfolio.dtos.Resume.ResumeUploadResponseDTO;
import com.portfolio.entities.NavLink;
import com.portfolio.entities.Project;
import com.portfolio.entities.Resume;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.RoleEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ResumeRepository;
import com.portfolio.services.CloudinaryService;
import com.portfolio.services.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
                .updatedAt(LocalDateTime.now())
                .build();
        Resume saved = resumeRepository.save(resume);
        return mapToResponse(saved);
    }

    @Override
    public Page<ResumeUploadResponseDTO> getByProfile(String profileId, StatusEnum status, Pageable pageable, String search, String sortDir, String sortBy){
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt");
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        boolean hasSearch = search != null && !search.isBlank();
        boolean hasStatus = status != null;
        boolean hasProfileId = profileId != null;
        Page<Resume> resumes;
        if(hasStatus && hasSearch && hasProfileId){
            resumes = resumeRepository.searchByProfileIdAndStatus(search,status,profileId,sortedPageable);
        }else if(hasStatus && hasSearch){
            resumes = resumeRepository.searchByStatus(search,status,sortedPageable);
        }else if(hasSearch && hasProfileId){
            resumes = resumeRepository.searchByProfileId(search,profileId,sortedPageable);
        }else if(hasStatus && hasProfileId){
            resumes = resumeRepository.findByStatusAndProfileId(status,profileId,sortedPageable);
        }else if(hasStatus){
            resumes = resumeRepository.findByStatus(status,sortedPageable);
        }else if(hasProfileId){
            resumes = resumeRepository.findByProfileId(profileId,sortedPageable);
        }else if(hasSearch){
            resumes = resumeRepository.search(search,sortedPageable);
        }else{
            resumes = resumeRepository.findAll(sortedPageable);
        }
        return resumes.map(this::mapToResponse);
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
        resume.setUpdatedAt(LocalDateTime.now());
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

    private ResumeUploadResponseDTO mapToResponse(Resume resume){
        return ResumeUploadResponseDTO.builder()
                .id(resume.getId())
                .fileName(resume.getFileName())
                .fileUrl(resume.getFileUrl())
                .status(resume.getStatus())
                .updatedAt(resume.getUpdatedAt())
                .build();
    }

}
