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
    private Integer id;
    private Integer logoId;
    private String logoName;
    private String logoUrl;
    private SkillCategoryEnum category;
    private SkillLevelEnum level;
}
