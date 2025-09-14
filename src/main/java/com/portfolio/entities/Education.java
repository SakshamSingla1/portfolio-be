package com.portfolio.entities;

import com.portfolio.enums.DegreeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="education",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"profile_id", "degree"})})
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String institution;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DegreeEnum degree;

    private String fieldOfStudy;
    private String location;
    private int startYear;
    private int endYear;
    private String grade;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
}
