package com.portfolio.entities;

import com.portfolio.enums.DegreeEnum;
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

import java.time.LocalDateTime;

@Document(collection = "educations")
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
public class Education {

    @Id
    private String id;

    @TextIndexed
    private String institution;
    private DegreeEnum degree;

    @TextIndexed
    private String fieldOfStudy;

    @TextIndexed
    private String location;
    private Integer startYear;
    private Integer endYear;
    private String grade;
    private String description;

    @Indexed
    private String profileId;

    @Indexed
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
