package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.DegreeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)

@Entity
@Table(name = "educations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Education extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String institution;

    @Enumerated(EnumType.STRING)
    private DegreeEnum degree;

    private String fieldOfStudy;
    private String location;
    private Integer startYear;
    private Integer endYear;
    private String grade;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;
}
