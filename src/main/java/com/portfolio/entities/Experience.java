package com.portfolio.entities;

import com.portfolio.enums.EmploymentStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Document(collection = "experiences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
        @CompoundIndex(
                name = "idx_profile_updated",
                def = "{ 'profileId': 1, 'updatedAt': -1 }"
        )
})
public class Experience {
    @Id
    private String id;

    @Indexed
    private String profileId;

    @TextIndexed
    private String companyName;

    @TextIndexed
    private String jobTitle;

    @TextIndexed
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private EmploymentStatusEnum employmentStatus;
    private String description;
    private List<String> skillIds;

    @Indexed
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
