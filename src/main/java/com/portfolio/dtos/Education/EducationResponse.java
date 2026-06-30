package com.portfolio.dtos.Education;

import com.portfolio.dtos.AuditableResponse;
import com.portfolio.enums.DegreeEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class EducationResponse extends AuditableResponse {
    private Long id;
    private String institution;
    private DegreeEnum degree;
    private String fieldOfStudy;
    private String location;
    private Integer startYear;
    private Integer endYear;
    private String description;
    private String grade;

    public EducationResponse(Long id, DegreeEnum degree, String institution,
                             String fieldOfStudy, Integer startYear, String location,
                             Integer endYear, String description, String grade,
                             LocalDateTime createdAt, LocalDateTime updatedAt, Long createdBy,
                             Long updatedBy, String createdByName, String updatedByName) {
        super(createdAt, updatedAt, createdBy, updatedBy, createdByName, updatedByName);
        this.id = id;
        this.degree = degree;
        this.institution = institution;
        this.fieldOfStudy = fieldOfStudy;
        this.startYear = startYear;
        this.location = location;
        this.endYear = endYear;
        this.description = description;
        this.grade = grade;
    }
}