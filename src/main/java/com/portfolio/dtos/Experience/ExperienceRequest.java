package com.portfolio.dtos.Experience;

import com.portfolio.enums.EmploymentStatusEnum;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class ExperienceRequest {
    @NotBlank(message = "Company name is required")
    private String companyName;
    @NotBlank(message = "Job title is required")
    private String jobTitle;
    private String location;
    private String startDate;
    private String endDate;
    @NotNull(message = "Employment status is required")
    private EmploymentStatusEnum employmentStatus;
    private String description;
    private List<String> skillIds;
    private Long profileId;
}
