package com.portfolio.dtos;

import lombok.Data;

@Data
public class ExperienceRequest {
    private String companyName;
    private String jobTitle;
    private String location;
    private String startDate;
    private String endDate;
    private boolean currentlyWorking;
    private String description;
    private String technologiesUsed;
}

