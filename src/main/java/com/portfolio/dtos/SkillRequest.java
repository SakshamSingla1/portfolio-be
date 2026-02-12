// SkillRequest.java
package com.portfolio.dtos;

import com.portfolio.enums.SkillCategoryEnum;
import com.portfolio.enums.SkillLevelEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRequest {
    private String logoId;
    private SkillLevelEnum level;
    private SkillCategoryEnum category;
    private String profileId;
}
