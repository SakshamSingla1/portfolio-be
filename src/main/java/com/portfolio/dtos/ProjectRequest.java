package com.portfolio.dtos;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class ProjectRequest {
    private String projectName;
    private String projectDescription;
    private String projectLink;
    private List<Integer> technologiesUsed;  // Skill IDs from frontend
    private Date projectStartDate;
    private Date projectEndDate;
    private boolean currentlyWorking;
    private String projectImageUrl;
}
