package com.portfolio.dtos;

import com.portfolio.enums.DegreeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EducationResponse {
    private Integer id;
    private String institution;
    private DegreeEnum degree;
    private String fieldOfStudy;
    private String location;
    private int startYear;
    private int endYear;
    private String description;
    private String grade;
}
