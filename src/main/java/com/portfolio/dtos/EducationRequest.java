package com.portfolio.dtos;

import com.portfolio.enums.DegreeEnum;
import lombok.Data;

@Data
public class EducationRequest {
    private String institution;
    private DegreeEnum degree;
    private String fieldOfStudy;
    private String location;
    private Integer startYear;
    private Integer endYear;
    private String description;
    private String grade;
    private String profileId;
}