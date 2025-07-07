package com.portfolio.dtos;

import com.portfolio.enums.DegreeEnum;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EducationResponse {
    private int id;
    private String institution;
    private DegreeEnum degree;
    private String fieldOfStudy;
    private String location;
    private int startYear;
    private int endYear;
    private String description;
}
