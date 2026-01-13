package com.portfolio.entities;

import com.portfolio.enums.EmploymentStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Document(collection = "experiences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Experience {
    @Id
    private String id;
    private String profileId;
    private String companyName;
    private String jobTitle;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private EmploymentStatusEnum employmentStatus;
    private String description;
    private List<String> skillIds;
}
