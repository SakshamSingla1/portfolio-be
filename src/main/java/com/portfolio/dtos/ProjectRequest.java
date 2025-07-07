package com.portfolio.dtos;

import lombok.Data;

@Data
public class ProjectRequest {
    private String projectName;
    private String projectDescription;
    private String projectDuration;
    private String projectLink;
    private String technologiesUsed;
}
