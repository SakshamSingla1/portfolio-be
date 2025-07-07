package com.portfolio.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExperienceResponse {
    private int id;
    private String companyName;
    private String jobTitle;
    private String location;
    private String startDate;
    private String endDate;
    private boolean currentlyWorking;
    private String description;
    private String technologiesUsed;
}
