package com.portfolio.dtos.Education;

import com.portfolio.enums.DegreeEnum;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EducationRequest {
    @NotBlank(message = "Institution is required")
    private String institution;
    @NotNull(message = "Degree is required")
    private DegreeEnum degree;
    @NotBlank(message = "Field of study is required")
    private String fieldOfStudy;
    private String location;
    private Integer startYear;
    private Integer endYear;
    private String description;
    private String grade;
    private Long profileId;
}