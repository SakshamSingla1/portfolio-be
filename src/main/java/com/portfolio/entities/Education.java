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
@Table(name="education")
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String institution;
    private DegreeEnum degree;
    private String fieldOfStudy;
    private String location;
    private int startYear;
    private int endYear;

    @Column(length = 1000)
    private String description;
}
