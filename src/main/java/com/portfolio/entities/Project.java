package com.portfolio.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String projectName;
    private String projectDescription;
    private String projectDuration;
    private String projectLink;
    private String technologiesUsed;
    @Temporal(TemporalType.TIMESTAMP)
    private Date projectStartDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date projectEndDate;

    @PrePersist
    public void onCreate() {
        projectStartDate = new Date();
        projectEndDate = new Date();
    }

    @PreUpdate
    public void onUpdate() {
        projectEndDate = new Date();
    }

}
