package com.portfolio.servicesImpl;

import com.portfolio.dtos.ImageUploadResponse;
import com.portfolio.dtos.ProjectImages.ProjectImageRequest;
import com.portfolio.dtos.ProjectRequest;
import com.portfolio.dtos.ProjectResponse;
import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.entities.NotificationTemplate;
import com.portfolio.entities.Project;
import com.portfolio.entities.ProjectImages;
import com.portfolio.entities.Skill;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.WorkStatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.repositories.ProjectImageRepository;
import com.portfolio.repositories.ProjectRepository;
import com.portfolio.repositories.SkillRepository;
import com.portfolio.services.CloudinaryService;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final ProfileRepository profileRepository;
    private final CloudinaryService cloudinaryService;
    private final ProjectImageRepository projectImageRepository;

    @Override
    public ProjectResponse create(ProjectRequest req) throws GenericException {
        if (!profileRepository.existsById(req.getProfileId())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile not found");
        }
        if (projectRepository.existsByProjectNameAndProfileId(req.getProjectName(), req.getProfileId())) {
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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Project savedProject = projectRepository.save(project);
        saveProjectImages(
                savedProject.getId(),
                req.getProfileId(),
                req.getProjectImages()
        );
        return mapToResponse(savedProject);
    }

    @Override
    public ProjectResponse update(String id, ProjectRequest req) throws GenericException {
        Project project = projectRepository.findById(id)
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
        Project updatedProject = projectRepository.save(project);
        projectImageRepository.deleteByProjectId(id);
        saveProjectImages(
                id,
                req.getProfileId(),
                req.getProjectImages()
        );
        return mapToResponse(updatedProject);
    }

    @Override
    public ImageUploadResponse uploadProjectImage(String profileId, MultipartFile file) throws IOException, GenericException {
        profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        Map uploadResult = cloudinaryService.uploadProfileImage(file);
        return new ImageUploadResponse(uploadResult.get("secure_url").toString(), uploadResult.get("public_id").toString());
    }

    @Override
    public ProjectResponse getById(String id) throws GenericException {
        return projectRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROJECT_NOT_FOUND, "Project not found"));
    }

    @Override
    public String delete(String id) throws GenericException {
        if (!projectRepository.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.PROJECT_NOT_FOUND, "Project not found");
        }
        projectImageRepository.deleteByProjectId(id);
        projectRepository.deleteById(id);
        return "Project deleted successfully";
    }

    @Override
    public Page<ProjectResponse> getByProfile(String profileId, Pageable pageable, String search, String sortDir, String sortBy) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt");
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        boolean hasProfileId = profileId != null;
        boolean hasSearch = search != null && !search.isBlank();

        Page<Project> projects;
        if( hasSearch && hasProfileId){
            projects = projectRepository.findByProfileIdWithSearch(profileId,search,sortedPageable);
        }else if(hasSearch){
            projects = projectRepository.findBySearch(search,sortedPageable);
        }else if(hasProfileId) {
            projects = projectRepository.findByProfileId(profileId, sortedPageable);
        }else{
            projects = projectRepository.findAll(sortedPageable);
        }
        return projects.map(this::mapToResponse);
    }

    @Override
    public List<ProjectResponse> getByProfile(String profileId) {
        return projectRepository.findByProfileId(profileId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void saveProjectImages(String projectId, String profileId, List<ProjectImageRequest> images) {
        if (images == null || images.isEmpty()) return;
        List<ProjectImages> projectImages = images.stream()
                .map(img -> ProjectImages.builder()
                        .projectId(projectId)
                        .profileId(profileId)
                        .url(img.getUrl())
                        .publicId(img.getPublicId())
                        .build())
                .toList();
        projectImageRepository.saveAll(projectImages);
        }

    private ProjectResponse mapToResponse(Project project) {
        List<ProjectImages> images = projectImageRepository.findByProjectId(project.getId());
        List<Skill> skills = project.getSkillIds() == null ? List.of() : skillRepository.findAllById(project.getSkillIds());
        return ProjectResponse.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .projectDescription(project.getProjectDescription())
                .githubRepositories(project.getGithubRepositories())
                .projectLink(project.getProjectLink())
                .projectStartDate(project.getProjectStartDate())
                .projectEndDate(project.getProjectEndDate())
                .workStatus(project.getWorkStatus())
                .skills(skills.stream().map(skill -> new SkillDropdown(skill.getId(), skill.getLogo().getName(), skill.getLogo().getUrl())).toList())
                .projectImages(images.stream().map(img -> {
                    ProjectImageRequest dto = new ProjectImageRequest();
                    dto.setUrl(img.getUrl());
                    dto.setPublicId(img.getPublicId());
                    return dto;
                }).toList())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
