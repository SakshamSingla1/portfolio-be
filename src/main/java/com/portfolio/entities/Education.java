package com.portfolio.entities;

import com.portfolio.enums.DegreeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "educations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Education {

    @Id
    private String id;

    private String institution;
    private DegreeEnum degree;
    private String fieldOfStudy;
    private String location;
    private Integer startYear;
    private Integer endYear;
    private String grade;
    private String description;

    private String profileId;
}
