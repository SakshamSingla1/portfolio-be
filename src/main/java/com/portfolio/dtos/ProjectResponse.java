package com.portfolio.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ProjectResponse {
    private int id;
    private String projectName;
    private String projectDescription;
    private String projectDuration;
    private String projectLink;
    private String technologiesUsed;
    private Date projectStartDate;
    private Date projectEndDate;
}
