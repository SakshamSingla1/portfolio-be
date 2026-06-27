package com.portfolio.servicesImpl;

import com.portfolio.dtos.Resume.ResumeUploadResponseDTO;
import com.portfolio.entities.Resume;
import com.portfolio.entities.FileAsset;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.dtos.File.FileUploadRequest;
import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ResumeRepository;
import com.portfolio.repositories.FileAssetRepository;
import com.portfolio.services.FileService;
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
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final FileService fileService;
    private final FileAssetRepository fileAssetRepository;

    @Override
    public ResumeUploadResponseDTO uploadResume(Long profileId, MultipartFile file) throws IOException {
        resumeRepository.findByProfileIdAndStatus(profileId, StatusEnum.ACTIVE)
                .ifPresent(existing -> {
                    existing.setStatus(StatusEnum.INACTIVE);
                    resumeRepository.save(existing);
                });
        Resume resume = Resume.builder()
                .profileId(profileId)
                .status(StatusEnum.ACTIVE)
                .build();
        Resume saved = resumeRepository.save(resume);
 
        FileUploadRequest uploadReq = new FileUploadRequest();
        uploadReq.setResourceId(String.valueOf(saved.getId()));
        uploadReq.setResourceType(ResourceTypeEnum.RESUME);
        uploadReq.setPrimary(true);
        uploadReq.setMetaData(file.getOriginalFilename());
        try {
            fileService.upload(file, uploadReq);
        } catch (Exception e) {
            throw new IOException("Failed to upload resume file: " + e.getMessage(), e);
        }
        return mapToResponse(saved);
    }
 
    @Override
    public Page<ResumeUploadResponseDTO> getByProfile(Long profileId, StatusEnum status, Pageable pageable, String search, String sortDir, String sortBy){
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
    public void activateResume(Long profileId, Long resumeId) throws GenericException {
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
    public void deleteResume(Long resumeId) throws GenericException {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new GenericException(
                        ExceptionCodeEnum.RESUME_NOT_FOUND,
                        "Resume not found"
                ));
        try {
            fileService.deleteByResource(String.valueOf(resumeId), ResourceTypeEnum.RESUME.name());
        } catch (Exception ex) {
            // log.warn("File delete failed for resumeId={}", resumeId, ex);
        }
        resume.setStatus(StatusEnum.DELETED);
        resume.setUpdatedAt(LocalDateTime.now());
        resumeRepository.save(resume);
    }
 
    private ResumeUploadResponseDTO mapToResponse(Resume resume){
        String fileName = null;
        String fileUrl = null;
        String publicId = null;
 
        Optional<FileAsset> assetOpt = fileAssetRepository.findByResourceIdAndResourceTypeAndIsPrimaryTrue(String.valueOf(resume.getId()), ResourceTypeEnum.RESUME);
        if (assetOpt.isPresent()) {
            FileAsset asset = assetOpt.get();
            fileName = asset.getMetaData();
            if (fileName == null || fileName.isBlank()) {
                fileName = "resume";
            }
            fileUrl = asset.getPath();
            publicId = asset.getPublicId();
        }
 
        return ResumeUploadResponseDTO.builder()
                .id(resume.getId())
                .fileName(fileName)
                .fileUrl(fileUrl)
                .publicId(publicId)
                .status(resume.getStatus())
                .updatedAt(resume.getUpdatedAt())
                .build();
    }

}
