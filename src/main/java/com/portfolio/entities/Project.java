package com.portfolio.entities;

import com.portfolio.enums.WorkStatusEnum;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "projects")
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
public class Project {
    @Id
    private String id;
    @Indexed
    private String profileId;
    @TextIndexed
    private String projectName;
    private String projectDescription;
    private List<String> githubRepositories;
    private String projectLink;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;
    private WorkStatusEnum workStatus;
    private List<String> skillIds;
    @Indexed
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
