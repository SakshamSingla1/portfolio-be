package com.portfolio.dtos;

import com.portfolio.enums.EmploymentStatusEnum;
import lombok.Data;

import java.util.List;

@Data
public class ExperienceRequest {
    private String profileId;
    private String companyName;
    private String jobTitle;
    private String location;
    private String startDate;
    private String endDate;
    private EmploymentStatusEnum employmentStatus;
    private String description;
    private List<String> skillIds;
}
