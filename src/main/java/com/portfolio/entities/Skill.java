package com.portfolio.entities;

import com.portfolio.enums.SkillCategoryEnum;
import com.portfolio.enums.SkillLevelEnum;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "skills")
public class Skill {

    @Id
    private String id;
    private Logo logo;
    private SkillLevelEnum level;
    private SkillCategoryEnum category;
    private String profileId;
}
