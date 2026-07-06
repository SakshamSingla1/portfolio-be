package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.converters.StringListConverter;
import com.portfolio.enums.WorkStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    private String projectName;

    @Column(columnDefinition = "TEXT")
    private String projectDescription;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT", name = "github_repositories")
    private List<String> githubRepositories;

    private String projectLink;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;

    @Enumerated(EnumType.STRING)
    private WorkStatusEnum workStatus;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT", name = "skill_ids")
    private List<String> skillIds;
}
