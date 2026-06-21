// SkillResponse.java
package com.portfolio.dtos.Skill;

import com.portfolio.enums.SkillCategoryEnum;
import com.portfolio.enums.SkillLevelEnum;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillResponse {
    private Long id;
    private Long logoId;
    private String logoName;
    private String logoUrl;
    private SkillCategoryEnum category;
    private SkillLevelEnum level;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
