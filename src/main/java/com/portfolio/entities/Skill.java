package com.portfolio.entities;

import com.portfolio.enums.SkillCategoryEnum;
import com.portfolio.enums.SkillLevelEnum;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "skills")
@CompoundIndexes({
        @CompoundIndex(
                name = "idx_profile_logo_unique",
                def = "{ 'profileId': 1, 'logoId': 1 }",
                unique = true
        ),
        @CompoundIndex(
                name = "idx_profile_updated",
                def = "{ 'profileId': 1, 'updatedAt': -1 }"
        )
})

public class Skill {

    @Id
    private String id;
    private Logo logo;
    private SkillLevelEnum level;
    private SkillCategoryEnum category;
    @Indexed
    private String profileId;
    @Indexed
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
