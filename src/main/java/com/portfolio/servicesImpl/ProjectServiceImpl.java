package com.portfolio.servicesImpl;

import com.portfolio.dao.file.FileAssetDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dao.project.ProjectDao;
import com.portfolio.dao.skill.SkillDao;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.dtos.ProjectImages.ProjectImageRequest;
import com.portfolio.dtos.Project.ProjectRequest;
import com.portfolio.dtos.Project.ProjectResponse;
import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.entities.Project;
import com.portfolio.entities.FileAsset;
import com.portfolio.entities.Skill;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.WorkStatusEnum;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.dtos.File.FileUploadRequest;
import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.FileService;
import com.portfolio.services.ProjectService;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectDao projectDao;
    private final SkillDao skillDao;
    private final ProfileDao profileDao;
    private final FileService fileService;
    private final FileAssetDao fileAssetDao;

    @Override
    public ProjectResponse create(ProjectRequest req) throws GenericException {
        if (!profileDao.existsById(req.getProfileId())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile not found");
        }
        if (projectDao.existsByProjectNameAndProfileId(req.getProjectName(), req.getProfileId())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Project with same name already exists");
        }
        Project project = Project.builder()
                .profileId(req.getProfileId())
                .projectName(req.getProjectName())
                .projectDescription(req.getProjectDescription())
                .githubRepositories(req.getGithubRepositories())
                .projectLink(req.getProjectLink())
                .projectStartDate(req.getProjectStartDate())
                .projectEndDate(
                        req.getWorkStatus() == WorkStatusEnum.CURRENT
                                ? null
                                : req.getProjectEndDate()
                )
                .workStatus(req.getWorkStatus())
                .skillIds(req.getSkillIds())
                .build();
        Project savedProject = projectDao.save(project);
        saveProjectImages(
                savedProject.getId(),
                req.getProfileId(),
                req.getProjectImages()
        );
        return mapToResponse(savedProject);
    }

    @Override
    public ProjectResponse update(Long id, ProjectRequest req) throws GenericException {
        Project project = projectDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROJECT_NOT_FOUND, "Project not found"));
        project.setProjectName(req.getProjectName());
        project.setProjectDescription(req.getProjectDescription());
        project.setGithubRepositories(req.getGithubRepositories());
        project.setProjectLink(req.getProjectLink());
        project.setProjectStartDate(req.getProjectStartDate());
        project.setProjectEndDate(
                req.getWorkStatus() == WorkStatusEnum.CURRENT
                        ? null
                        : req.getProjectEndDate()
        );
        project.setWorkStatus(req.getWorkStatus());
        project.setSkillIds(req.getSkillIds());
        project.setUpdatedAt(LocalDateTime.now());
        Project updatedProject = projectDao.save(project);
        saveProjectImages(
                id,
                req.getProfileId(),
                req.getProjectImages()
        );
        return mapToResponse(updatedProject);
    }

    @Override
    public ImageUploadResponse uploadProjectImage(Long profileId, MultipartFile file) throws IOException, GenericException {
        profileDao.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        FileUploadRequest uploadReq = new FileUploadRequest();
        uploadReq.setResourceId(profileId.intValue());
        uploadReq.setResourceType(ResourceTypeEnum.PROJECT);
        uploadReq.setPrimary(false);
        try {
            FileAssetDTO asset = fileService.upload(file, uploadReq);
            return new ImageUploadResponse(asset.getPath(), asset.getPublicId());
        } catch (Exception e) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Failed to upload project image: " + e.getMessage());
        }
    }

    @Override
    public ProjectResponse getById(Long id) throws GenericException {
        return projectDao.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROJECT_NOT_FOUND, "Project not found"));
    }

    @Override
    public String delete(Long id) throws GenericException {
        if (!projectDao.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.PROJECT_NOT_FOUND, "Project not found");
        }
        try {
            fileService.deleteByResource(id.intValue(), ResourceTypeEnum.PROJECT.name());
        } catch (Exception ignored) {}
        projectDao.deleteById(id);
        return "Project deleted successfully";
    }

    @Override
    public Page<ProjectResponse> getByProfile(Long profileId, Pageable pageable, String search, String sortDir, String sortBy) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt");
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        return projectDao.findByCriteria(profileId, search, sortedPageable).map(this::mapToResponse);
    }

    @Override
    public List<ProjectResponse> getByProfile(Long profileId) {
        return projectDao.findByProfileId(profileId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void saveProjectImages(Long projectId, Long profileId, List<ProjectImageRequest> images) {
        List<FileAsset> existingAssets = fileAssetDao.findByResourceIdAndResourceTypeOrderBySortOrderAsc(projectId.intValue(), ResourceTypeEnum.PROJECT);

        List<Long> targetAssetIds = new java.util.ArrayList<>();
        List<ProjectImageRequest> imagesList = images == null ? List.of() : images;

        for (ProjectImageRequest img : imagesList) {
            if (img.getPublicId() == null || img.getPublicId().isBlank()) continue;
            Optional<FileAsset> assetOpt = Optional.empty();
            try {
                Long assetId = Long.parseLong(img.getPublicId());
                assetOpt = fileAssetDao.findById(assetId);
            } catch (NumberFormatException e) {
                assetOpt = fileAssetDao.findByPublicId(img.getPublicId());
            }

            if (assetOpt.isEmpty() && img.getUrl() != null && !img.getUrl().isBlank()) {
                assetOpt = fileAssetDao.findByPath(img.getUrl());
            }

            assetOpt.ifPresent(asset -> targetAssetIds.add(asset.getId()));
        }

        for (FileAsset asset : existingAssets) {
            if (!targetAssetIds.contains(asset.getId())) {
                try {
                    fileService.delete(asset.getId());
                } catch (Exception ignored) {}
            }
        }

        if (images == null || images.isEmpty()) return;

        int order = 0;
        for (ProjectImageRequest img : images) {
            Optional<FileAsset> assetOpt = Optional.empty();
            try {
                Long assetId = Long.parseLong(img.getPublicId());
                assetOpt = fileAssetDao.findById(assetId);
            } catch (NumberFormatException e) {
                assetOpt = fileAssetDao.findByPublicId(img.getPublicId());
            }

            if (assetOpt.isEmpty() && img.getUrl() != null && !img.getUrl().isBlank()) {
                assetOpt = fileAssetDao.findByPath(img.getUrl());
            }

            if (assetOpt.isPresent()) {
                FileAsset asset = assetOpt.get();
                asset.setResourceId(projectId.intValue());
                asset.setSortOrder(order++);
                fileAssetDao.save(asset);
            } else {
                FileAsset asset = new FileAsset();
                asset.setResourceId(projectId.intValue());
                asset.setResourceType(ResourceTypeEnum.PROJECT);
                asset.setPath(img.getUrl());
                asset.setPublicId(img.getPublicId());
                asset.setSortOrder(order++);
                fileAssetDao.save(asset);
            }
        }
    }

    private ProjectResponse mapToResponse(Project project) {
        List<FileAsset> images = fileAssetDao.findByResourceIdAndResourceTypeOrderBySortOrderAsc(project.getId().intValue(), ResourceTypeEnum.PROJECT);
        List<Long> skillIdLongs = project.getSkillIds() == null ? List.of() : project.getSkillIds().stream()
                .map(Long::valueOf)
                .toList();
        List<Skill> skills = skillIdLongs.isEmpty() ? List.of() : skillDao.findAllById(skillIdLongs);

        List<SkillDropdown> skillDropdowns = skills.stream().map(skill -> {
            String logoUrl = fileAssetDao.findByResourceIdAndResourceTypeAndIsPrimaryTrue(skill.getLogoId().intValue(), ResourceTypeEnum.LOGO)
                    .map(FileAsset::getPath)
                    .orElse(null);
            return new SkillDropdown(skill.getId(), skill.getLogoName(), logoUrl);
        }).toList();

        return ProjectResponse.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .projectDescription(project.getProjectDescription())
                .githubRepositories(project.getGithubRepositories())
                .projectLink(project.getProjectLink())
                .projectStartDate(project.getProjectStartDate())
                .projectEndDate(project.getProjectEndDate())
                .workStatus(project.getWorkStatus())
                .skills(skillDropdowns)
                .projectImages(images.stream().map(img -> {
                    ProjectImageRequest dto = new ProjectImageRequest();
                    dto.setUrl(img.getPath());
                    dto.setPublicId(img.getPublicId());
                    return dto;
                }).toList())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
