package com.portfolio.dtos;

import com.portfolio.dtos.ProjectImages.ProjectImageRequest;
import com.portfolio.enums.WorkStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ProjectRequest {
    private String profileId;
    private String projectName;
    private String projectDescription;
    private List<String> githubRepositories;
    private String projectLink;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;
    private WorkStatusEnum workStatus;
    private List<String> skillIds;
    private List<ProjectImageRequest> projectImages;
}
