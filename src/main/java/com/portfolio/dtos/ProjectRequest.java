package com.portfolio.dtos;

import com.portfolio.enums.WorkStatusEnum;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProjectRequest {
    private String profileId;
    private String projectName;
    private String projectDescription;
    private String projectLink;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;
    private WorkStatusEnum workStatus;
    private String projectImageUrl;
    private List<String> skillIds;
}
