// SkillResponse.java
package com.portfolio.dtos;

import com.portfolio.enums.SkillCategoryEnum;
import com.portfolio.enums.SkillLevelEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillResponse {
    private String id;
    private String logoId;
    private String logoName;
    private String logoUrl;
    private SkillCategoryEnum category;
    private SkillLevelEnum level;
}
