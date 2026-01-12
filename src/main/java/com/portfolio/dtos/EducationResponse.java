package com.portfolio.dtos;

import com.portfolio.enums.DegreeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EducationResponse {
    private String id;
    private String institution;
    private DegreeEnum degree;
    private String fieldOfStudy;
    private String location;
    private Integer startYear;
    private Integer endYear;
    private String description;
    private String grade;
}