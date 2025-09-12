package com.portfolio.dtos;

import com.portfolio.enums.DegreeEnum;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class EducationRequest {
    private String institution;
    private DegreeEnum degree;
    private String fieldOfStudy;
    private String location;
    private int startYear;
    private int endYear;
    private String description;
    private String grade;
}
