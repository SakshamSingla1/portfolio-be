package com.portfolio.entities;

import com.portfolio.enums.WorkStatusEnum;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "projects")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    private String id;
    private String profileId;
    private String projectName;
    private String projectDescription;
    private List<String> githubRepositories;
    private String projectLink;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;
    private WorkStatusEnum workStatus;
    private List<String> skillIds;
}
