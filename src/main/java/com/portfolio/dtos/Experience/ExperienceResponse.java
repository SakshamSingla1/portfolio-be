package com.portfolio.dtos.Experience;

import com.portfolio.dtos.AuditableResponse;
import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.enums.EmploymentStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExperienceResponse extends AuditableResponse {
    private Long id;
    private String companyName;
    private String jobTitle;
    private String location;
    private String startDate;
    private String endDate;
    private EmploymentStatusEnum employmentStatus;
    private String description;
    private List<SkillDropdown> skills;

    public ExperienceResponse(Long id, String companyName, String jobTitle, String location,
                              LocalDate startDate, LocalDate endDate,
                              EmploymentStatusEnum employmentStatus, String description,
                              LocalDateTime createdAt, LocalDateTime updatedAt,
                              Long createdBy, Long updatedBy,
                              String createdByName, String updatedByName) {
        super(createdAt, updatedAt, createdBy, updatedBy, createdByName, updatedByName);
        this.id = id;
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.location = location;
        this.startDate = startDate != null ? startDate.toString() : null;
        this.endDate = endDate != null ? endDate.toString() : null;
        this.employmentStatus = employmentStatus;
        this.description = description;
        this.skills = List.of();
    }
}
